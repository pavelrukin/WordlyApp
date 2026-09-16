package com.rukinpavel.wordlyapp.ads

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.rukinpavel.wordlyapp.R
import com.rukinpavel.wordlyapp.core.domain.repository.AdManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    private val application: Application,
    private val activityProvider: ActivityProvider,
) : AdManager {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    init {
        loadRewardedAd(application)
    }

    private fun loadRewardedAd(application: Application) {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        val unitId = application.getString(R.string.admob_rewarded_unit_id)

        RewardedAd.load(
            application,
            unitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdManager", "Ad failed to load: ${adError.message}, code: ${adError.code}")
                    rewardedAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
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
            onError()
            loadRewardedAd(activity.application)
            Toast.makeText(activity, "Реклама еще загружается, попробуйте через секунду", Toast.LENGTH_SHORT).show()
        }
    }
}
