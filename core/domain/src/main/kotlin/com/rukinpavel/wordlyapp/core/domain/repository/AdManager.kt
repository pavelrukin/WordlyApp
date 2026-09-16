package com.rukinpavel.wordlyapp.core.domain.repository

interface AdManager {
    fun showRewardedAd(onRewarded: () -> Unit, onError: () -> Unit)
}
