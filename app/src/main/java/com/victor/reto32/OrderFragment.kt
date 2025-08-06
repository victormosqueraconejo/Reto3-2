package com.victor.reto32

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class OrderFragment : Fragment() {

    private lateinit var categoriesLayout: LinearLayout
    private lateinit var productsLayout: LinearLayout
    private lateinit var cartIcon: TextView
    private lateinit var totalQuantity: TextView
    private lateinit var totalPrice: TextView
    private lateinit var submitButton: Button

    private val apiService = ApiService()
    private var allProducts = listOf<Product>()
    private var selectedCategory = "all"
    private val categories = listOf("all", "electronics", "jewelery", "men's clothing", "women's clothing")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadCategories()
        loadProducts()
        updateCartInfo()
    }

    private fun initViews(view: View) {
        categoriesLayout = view.findViewById(R.id.categories_layout)
        productsLayout = view.findViewById(R.id.products_layout)
        cartIcon = view.findViewById(R.id.cart_icon)
        totalQuantity = view.findViewById(R.id.total_quantity)
        totalPrice = view.findViewById(R.id.total_price)
        submitButton = view.findViewById(R.id.submit_button)

        cartIcon.setOnClickListener {
            if (CartManager.getTotalItems() > 0) {
                (activity as MainActivity).replaceFragment(CartFragment())
            }
        }

        submitButton.setOnClickListener {
            if (CartManager.getTotalItems() > 0) {
                (activity as MainActivity).replaceFragment(InformationFragment())
            } else {
                Toast.makeText(context, "Agrega items al carrito primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories() {
        categories.forEach { category ->
            val categoryView = LayoutInflater.from(context).inflate(R.layout.item_category, categoriesLayout, false)
            val categoryButton = categoryView.findViewById<Button>(R.id.category_button)

            categoryButton.text = category.capitalize()
            categoryButton.isSelected = category == selectedCategory

            categoryButton.setOnClickListener {
                selectedCategory = category
                updateCategorySelection()
                filterProducts()
            }

            categoriesLayout.addView(categoryView)
        }
    }

    private fun updateCategorySelection() {
        for (i in 0 until categoriesLayout.childCount) {
            val categoryView = categoriesLayout.getChildAt(i)
            val button = categoryView.findViewById<Button>(R.id.category_button)
            button.isSelected = button.text.toString().lowercase() == selectedCategory
        }
    }

    private fun loadProducts() {
        apiService.getProducts(object : ApiService.ProductCallback {
            override fun onSuccess(products: List<Product>) {
                activity?.runOnUiThread {
                    allProducts = products
                    filterProducts()
                }
            }

            override fun onError(error: String) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun filterProducts() {
        val filteredProducts = if (selectedCategory == "all") {
            allProducts
        } else {
            allProducts.filter { it.category == selectedCategory }
        }

        productsLayout.removeAllViews()

        if (filteredProducts.isEmpty()) {
            val nothingView = LayoutInflater.from(context).inflate(R.layout.nothing_view, productsLayout, false)
            productsLayout.addView(nothingView)
        } else {
            filteredProducts.forEach { product ->
                addProductView(product)
            }
        }
    }

    private fun addProductView(product: Product) {
        val productView = LayoutInflater.from(context).inflate(R.layout.item_product, productsLayout, false)

        val nameText = productView.findViewById<TextView>(R.id.product_name)
        val descriptionText = productView.findViewById<TextView>(R.id.product_description)
        val priceText = productView.findViewById<TextView>(R.id.product_price)
        val decreaseBtn = productView.findViewById<Button>(R.id.decrease_button)
        val increaseBtn = productView.findViewById<Button>(R.id.increase_button)
        val quantityText = productView.findViewById<TextView>(R.id.quantity_text)

        nameText.text = product.name
        descriptionText.text = product.description
        priceText.text = "$${product.price}"

        val cartItem = CartManager.getCartItems().find { it.product.id == product.id }
        val currentQuantity = cartItem?.quantity ?: 0

        quantityText.text = currentQuantity.toString()
        decreaseBtn.visibility = if (currentQuantity < 1) View.GONE else View.VISIBLE

        increaseBtn.setOnClickListener {
            CartManager.addItem(product)
            updateProductQuantity(productView, product)
            updateCartInfo()

            // Aplicar descuento si es necesario
            val newQuantity = CartManager.getCartItems().find { it.product.id == product.id }?.quantity ?: 0
            if (newQuantity >= 5) {
                applyDiscount(productView, product)
            }
        }

        decreaseBtn.setOnClickListener {
            CartManager.removeItem(product.id)
            updateProductQuantity(productView, product)
            updateCartInfo()
        }

        productsLayout.addView(productView)
    }

    private fun updateProductQuantity(productView: View, product: Product) {
        val quantityText = productView.findViewById<TextView>(R.id.quantity_text)
        val decreaseBtn = productView.findViewById<Button>(R.id.decrease_button)

        val cartItem = CartManager.getCartItems().find { it.product.id == product.id }
        val quantity = cartItem?.quantity ?: 0

        quantityText.text = quantity.toString()
        decreaseBtn.visibility = if (quantity < 1) View.GONE else View.VISIBLE
    }

    private fun applyDiscount(productView: View, product: Product) {
        val priceText = productView.findViewById<TextView>(R.id.product_price)
        val originalPrice = product.price
        val discountedPrice = originalPrice * 0.9 // 10% descuento

        priceText.text = "$${String.format("%.2f", discountedPrice)}"
        priceText.setTextColor(resources.getColor(android.R.color.holo_red_dark))
    }

    private fun updateCartInfo() {
        val totalItems = CartManager.getTotalItems()
        val total = CartManager.getTotalPrice()

        totalQuantity.text = totalItems.toString()
        totalQuantity.visibility = if (totalItems > 0) View.VISIBLE else View.GONE
        totalPrice.text = "$${String.format("%.2f", total)}"
    }
}