package com.victor.reto32

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class CartFragment : Fragment() {

    private lateinit var cartItemsLayout: LinearLayout
    private lateinit var totalPrice: TextView
    private lateinit var clearButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadCartItems()
        updateTotal()
    }

    private fun initViews(view: View) {
        cartItemsLayout = view.findViewById(R.id.cart_items_layout)
        totalPrice = view.findViewById(R.id.total_price)
        clearButton = view.findViewById(R.id.clear_button)

        clearButton.setOnClickListener {
            showClearConfirmationDialog()
        }

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadCartItems() {
        cartItemsLayout.removeAllViews()

        val cartItems = CartManager.getCartItems()

        cartItems.forEach { cartItem ->
            addCartItemView(cartItem)
        }
    }

    private fun addCartItemView(cartItem: CartItem) {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_cart, cartItemsLayout, false)

        val nameText = itemView.findViewById<TextView>(R.id.item_name)
        val priceText = itemView.findViewById<TextView>(R.id.item_price)
        val quantityText = itemView.findViewById<TextView>(R.id.item_quantity)
        val decreaseBtn = itemView.findViewById<Button>(R.id.decrease_button)
        val increaseBtn = itemView.findViewById<Button>(R.id.increase_button)

        nameText.text = cartItem.product.name

        // Aplicar descuento si la cantidad es >= 5
        val finalPrice = if (cartItem.quantity >= 5) {
            cartItem.product.price * 0.9
        } else {
            cartItem.product.price
        }

        if (cartItem.quantity >= 5) {
            priceText.text = "$${String.format("%.2f", finalPrice)} (10% OFF)"
            priceText.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        } else {
            priceText.text = "$${String.format("%.2f", finalPrice)}"
        }

        quantityText.text = cartItem.quantity.toString()

        increaseBtn.setOnClickListener {
            CartManager.addItem(cartItem.product)
            loadCartItems()
            updateTotal()
        }

        decreaseBtn.setOnClickListener {
            CartManager.removeItem(cartItem.product.id)
            loadCartItems()
            updateTotal()
        }

        cartItemsLayout.addView(itemView)
    }

    private fun updateTotal() {
        val total = calculateTotalWithDiscounts()
        totalPrice.text = "$${String.format("%.2f", total)}"
    }

    private fun calculateTotalWithDiscounts(): Double {
        return CartManager.getCartItems().sumOf { cartItem ->
            val price = if (cartItem.quantity >= 5) {
                cartItem.product.price * 0.9
            } else {
                cartItem.product.price
            }
            price * cartItem.quantity
        }
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Order")
            .setMessage("Do you want to empty the cart?")
            .setPositiveButton("Confirm") { _, _ ->
                CartManager.clearCart()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}