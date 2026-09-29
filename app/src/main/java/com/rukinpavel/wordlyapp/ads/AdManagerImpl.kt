package com.rukinpavel.wordlyapp.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rukinpavel.wordlyapp.BuildConfig
import com.rukinpavel.wordlyapp.R
import com.rukinpavel.wordlyapp.core.domain.repository.AdManager
import com.rukinpavel.wordlyapp.core.platform.android.ActivityProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    private val application: Application,
    private val activityProvider: ActivityProvider,
) : AdManager {

    private sealed class LoadedAd {
        data class Rewarded(val ad: RewardedAd) : LoadedAd()
        data class RewardedInterstitial(val ad: RewardedInterstitialAd) : LoadedAd()

        fun show(
            activity: Activity,
            onRewarded: () -> Unit,
            onDismissed: () -> Unit,
            onError: (AdError) -> Unit,
        ) {
            when (this) {
                is Rewarded -> {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            onDismissed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            onError(adError)
                        }
                    }
                    ad.show(activity) { onRewarded() }
                }

                is RewardedInterstitial -> {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            onDismissed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            onError(adError)
                        }
                    }
                    ad.show(activity) { onRewarded() }
                }
            }
        }
    }

    private var loadedAd: LoadedAd? = null
    private var isLoading = false
    private val pendingCallbacks = mutableListOf<(Boolean) -> Unit>()

    init {
        try {
            if (BuildConfig.DEBUG) {
                val testDeviceIds = listOf(
                    AdRequest.DEVICE_ID_EMULATOR,
                    "6C495C26EF9C34868949A080DE386002",
                )
                val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
                MobileAds.setRequestConfiguration(configuration)
            }
            MobileAds.initialize(application)
        } catch (e: Exception) {
            Log.e("AdManager", "Error initializing MobileAds", e)
        }
        loadAd()
    }

    private fun loadAd(onResult: ((Boolean) -> Unit)? = null) {
        if (loadedAd != null) {
            onResult?.invoke(true)
            return
        }

        if (onResult != null) {
            pendingCallbacks.add(onResult)
        }

        if (isLoading) return
        isLoading = true

        val context = activityProvider.getActivity() ?: application
        val unitId = application.getString(R.string.admob_rewarded_unit_id)
        val adRequest = AdRequest.Builder().build()

        // First try standard RewardedAd
        RewardedAd.load(
            context,
            unitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdManager", "RewardedAd loaded successfully")
                    loadedAd = LoadedAd.Rewarded(ad)
                    notifyLoadResult(true)
                }

                override fun onAdFailedToLoad(rewardedError: LoadAdError) {
                    Log.w(
                        "AdManager",
                        "RewardedAd failed: ${rewardedError.message}, code: ${rewardedError.code}. Trying RewardedInterstitialAd...",
                    )
                    // Fallback to RewardedInterstitialAd
                    RewardedInterstitialAd.load(
                        context,
                        unitId,
                        adRequest,
                        object : RewardedInterstitialAdLoadCallback() {
                            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                                Log.d("AdManager", "RewardedInterstitialAd loaded successfully")
                                loadedAd = LoadedAd.RewardedInterstitial(ad)
                                notifyLoadResult(true)
                            }

                            override fun onAdFailedToLoad(interstitialError: LoadAdError) {
                                Log.e(
                                    "AdManager",
                                    "RewardedInterstitialAd also failed: ${interstitialError.message}, code: ${interstitialError.code}",
                                )
                                loadedAd = null
                                notifyLoadResult(false)
                            }
                        },
                    )
                }
            },
        )
    }

    private fun notifyLoadResult(success: Boolean) {
        isLoading = false
        val callbacks = pendingCallbacks.toList()
        pendingCallbacks.clear()
        callbacks.forEach { it.invoke(success) }
    }

    override fun showRewardedAd(onRewarded: () -> Unit, onError: () -> Unit) {
        val activity = activityProvider.getActivity()
        if (activity == null) {
            Log.e("AdManager", "Cannot show ad: currentActivity is null")
            onError()
            return
        }

        val currentAd = loadedAd
        if (currentAd != null) {
            loadedAd = null
            currentAd.show(
                activity = activity,
                onRewarded = onRewarded,
                onDismissed = {
                    Log.d("AdManager", "Ad dismissed")
                    loadAd()
                },
                onError = { adError ->
                    Log.e("AdManager", "Ad failed to show: ${adError.message}")
                    onError()
                    loadAd()
                },
            )
        } else {
            Log.w("AdManager", "Ad not ready yet, loading on demand...")
            loadAd { success ->
                if (success) {
                    val newAd = loadedAd
                    if (newAd != null) {
                        loadedAd = null
                        newAd.show(
                            activity = activity,
                            onRewarded = onRewarded,
                            onDismissed = {
                                Log.d("AdManager", "Ad dismissed")
                                loadAd()
                            },
                            onError = { adError ->
                                Log.e("AdManager", "Ad failed to show: ${adError.message}")
                                onError()
                                loadAd()
                            },
                        )
                    } else {
                        onError()
                    }
                } else {
                    onError()
                }
            }
        }
    }

    override fun isVpnActive(): Boolean {
        return try {
            val connectivityManager =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } catch (_: Exception) {
            false
        }
    }
}
