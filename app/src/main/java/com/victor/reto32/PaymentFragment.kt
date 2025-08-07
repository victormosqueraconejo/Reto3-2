package com.victor.reto32

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PaymentFragment : Fragment() {

    private lateinit var confirmButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        confirmButton = view.findViewById(R.id.confirm_button)
        backButton = view.findViewById(R.id.back_button)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        confirmButton.setOnClickListener {

            CartManager.clearCart()


            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            (activity as MainActivity).replaceFragment(OrderFragment())
        }
    }
}