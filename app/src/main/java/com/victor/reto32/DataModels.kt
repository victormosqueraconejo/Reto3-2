package com.victor.reto32


data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addItem(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(product, 1))
        }
    }

    fun removeItem(productId: Int) {
        val item = cartItems.find { it.product.id == productId }
        item?.let {
            if (it.quantity > 1) {
                it.quantity--
            } else {
                cartItems.remove(it)
            }
        }
    }

    fun getCartItems(): List<CartItem> = cartItems.toList()

    fun getTotalItems(): Int = cartItems.sumOf { it.quantity }

    fun getTotalPrice(): Double = cartItems.sumOf { it.product.price * it.quantity }

    fun clearCart() {
        cartItems.clear()
    }
}