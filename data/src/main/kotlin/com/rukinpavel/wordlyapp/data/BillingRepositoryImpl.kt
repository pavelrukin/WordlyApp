package com.rukinpavel.wordlyapp.data

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.rukinpavel.wordlyapp.core.model.SubscriptionOption
import com.rukinpavel.wordlyapp.core.model.SubscriptionType
import com.rukinpavel.wordlyapp.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class BillingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BillingRepository,
    PurchasesUpdatedListener {

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

            override fun onBillingServiceDisconnected() {
                // Try to reconnect
            }
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
                // В DEBUG режиме добавляем тестовые данные, если Google Play не вернул продукты
                _subscriptionOptions.value = listOf(
                    SubscriptionOption(
                        "weekly_subscription",
                        "Weekly (Test)",
                        "$0.99",
                        "Test weekly sub",
                        SubscriptionType.WEEKLY,
                    ),
                    SubscriptionOption(
                        "monthly_subscription",
                        "Monthly (Test)",
                        "$2.99",
                        "Test monthly sub",
                        SubscriptionType.MONTHLY,
                    ),
                    SubscriptionOption(
                        "yearly_subscription",
                        "Yearly (Test)",
                        "$19.99",
                        "Test yearly sub",
                        SubscriptionType.YEARLY,
                    ),
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
                    price =
                    details.subscriptionOfferDetails?.firstOrNull()
                        ?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                        ?: "N/A",
                    description = details.description,
                    type = type,
                )
            }
            _subscriptionOptions.value = options
        }
    }

    override suspend fun purchaseSubscription(option: SubscriptionOption) {
        // Заглушка, так как ActivityProvider доступен только в :app модуле
        // Для полноценной работы в многомодульном проекте лучше передавать Activity в метод
    }

    override suspend fun restorePurchases() {
        // Implementation for restoring
    }

    private fun checkActiveSubscriptions() {
        // Implementation for checking
    }

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
