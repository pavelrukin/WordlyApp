package com.rukinpavel.wordlyapp.core.model

data class SubscriptionOption(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val type: SubscriptionType,
)

enum class SubscriptionType {
    WEEKLY,
    MONTHLY,
    YEARLY,
}
