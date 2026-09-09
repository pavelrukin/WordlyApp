package com.rukinpavel.wordlyapp.domain.repository

interface AdManager {
    fun showRewardedAd(onRewarded: () -> Unit, onError: () -> Unit)
}
