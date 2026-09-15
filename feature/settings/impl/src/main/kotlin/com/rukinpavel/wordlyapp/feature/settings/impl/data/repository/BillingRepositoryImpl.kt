package com.rukinpavel.wordlyapp.feature.settings.impl.data.repository

import android.content.Context
import com.android.billingclient.api.*
import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.core.model.SubscriptionOption
import com.rukinpavel.wordlyapp.core.model.SubscriptionType
import com.rukinpavel.wordlyapp.feature.settings.impl.BuildConfig
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.BillingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BillingRepository, PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var productDetailsMap = mutableMapOf<String, ProductDetails>()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _subscriptionOptions = MutableStateFlow<List<SubscriptionOption>>(emptyList())
    override val subscriptionOptions = _subscriptionOptions.asStateFlow()

    override val isPremium = userPreferencesRepository.isPremium

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    checkActiveSubscriptions()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("weekly_subscription")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("monthly_subscription")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("yearly_subscription")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { _, result ->
            val detailsList = result.productDetailsList

            if (detailsList.isEmpty() && BuildConfig.DEBUG) {
                _subscriptionOptions.value = listOf(
                    SubscriptionOption("weekly_subscription", "Weekly (Test)", "$0.99", "Test weekly sub", SubscriptionType.WEEKLY),
                    SubscriptionOption("monthly_subscription", "Monthly (Test)", "$2.99", "Test monthly sub", SubscriptionType.MONTHLY),
                    SubscriptionOption("yearly_subscription", "Yearly (Test)", "$19.99", "Test yearly sub", SubscriptionType.YEARLY),
                )
                return@queryProductDetailsAsync
            }

            detailsList.forEach { details ->
                productDetailsMap[details.productId] = details
            }

            val options = detailsList.map { details ->
                val type = when (details.productId) {
                    "weekly_subscription" -> SubscriptionType.WEEKLY
                    "monthly_subscription" -> SubscriptionType.MONTHLY
                    else -> SubscriptionType.YEARLY
                }
                SubscriptionOption(
                    id = details.productId,
                    name = details.name,
                    price = details.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: "N/A",
                    description = details.description,
                    type = type,
                )
            }
            _subscriptionOptions.value = options
        }
    }

    override suspend fun purchaseSubscription(option: SubscriptionOption) {}

    override suspend fun restorePurchases() {}

    private fun checkActiveSubscriptions() {}

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            scope.launch {
                userPreferencesRepository.updatePremiumStatus(true)
            }
        }
    }
}
