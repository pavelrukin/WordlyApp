package com.rukinpavel.wordlyapp.ads

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
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

    private var rewardedAd: RewardedInterstitialAd? = null
    private var isLoading = false

    init {
        loadRewardedAd(application)
    }

    private fun loadRewardedAd(application: Application) {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        val unitId = application.getString(R.string.admob_rewarded_unit_id)

        RewardedInterstitialAd.load(
            application,
            unitId,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdManager", "Ad failed to load: ${adError.message}, code: ${adError.code}")
                    rewardedAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d("AdManager", "Ad loaded successfully")
                    rewardedAd = ad
                    isLoading = false
                }
            },
        )
    }

    override fun showRewardedAd(onRewarded: () -> Unit, onError: () -> Unit) {
        val activity = activityProvider.getActivity()
        if (activity == null) {
            Log.e("AdManager", "Cannot show ad: currentActivity is null")
            onError()
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdManager", "Ad dismissed")
                    rewardedAd = null
                    loadRewardedAd(activity.application)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdManager", "Ad failed to show: ${adError.message}")
                    rewardedAd = null
                    onError()
                    loadRewardedAd(activity.application)
                }
            }
            ad.show(activity) {
                Log.d("AdManager", "User earned reward")
                onRewarded()
            }
        } else {
            Log.w("AdManager", "Ad not ready yet, attempting to load...")
            loadRewardedAd(activity.application)
        }
    }
}
