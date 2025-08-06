package com.victor.reto32

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ApiService {
    private val client = OkHttpClient()

    interface ProductCallback {
        fun onSuccess(products: List<Product>)
        fun onError(error: String)
    }

    fun getProducts(callback: ProductCallback) {
        val request = Request.Builder()
            .url("https://fakestoreapi.com/products")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Error de red")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonString = responseBody.string()
                    val products = parseProducts(jsonString)
                    callback.onSuccess(products)
                } ?: callback.onError("Respuesta vacía")
            }
        })
    }

    private fun parseProducts(jsonString: String): List<Product> {
        val products = mutableListOf<Product>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            val product = Product(
                id = json.getInt("id"),
                name = json.getString("title"),
                description = json.getString("description"),
                price = json.getDouble("price"),
                category = json.getString("category"),
                image = json.getString("image")
            )
            products.add(product)
        }

        return products
    }
}