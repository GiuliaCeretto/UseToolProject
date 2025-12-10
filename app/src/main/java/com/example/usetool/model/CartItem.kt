package com.example.usetool.model

data class CartItem(
    val id: String,
    val tool: Tool,
    val distributorId: String?,
    var quantity: Int = 1,
    var durationHours: Int = 24
) {
    val subtotal: Double get() = tool.pricePerHour * durationHours * quantity
}
