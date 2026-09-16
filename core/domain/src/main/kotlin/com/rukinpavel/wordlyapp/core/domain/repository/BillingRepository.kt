package com.rukinpavel.wordlyapp.core.domain.repository

import com.rukinpavel.wordlyapp.core.model.SubscriptionOption
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionOptions: Flow<List<SubscriptionOption>>
    val isPremium: Flow<Boolean>

    suspend fun purchaseSubscription(option: SubscriptionOption)
    suspend fun restorePurchases()
}
