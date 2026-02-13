package com.example.usetool.model

data class CartItem(
    val id: String,
    val tool: Tool,
    val distributorId: String? = null,
    val quantity: Int = 1,
    val durationHours: Int = 24
) {
    val subtotal: Double
        get() = when {
            tool.available && tool.pricePerHour != null ->
                tool.pricePerHour * durationHours * quantity

            tool.available && tool.purchasePrice != null ->
                tool.purchasePrice * quantity

            else -> 0.0
        }
}

