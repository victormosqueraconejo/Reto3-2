package com.victor.reto32


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class InformationFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var visaRadio: RadioButton
    private lateinit var cashRadio: RadioButton
    private lateinit var checkoutButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        nameEditText = view.findViewById(R.id.name_edit_text)
        phoneEditText = view.findViewById(R.id.phone_edit_text)
        addressEditText = view.findViewById(R.id.address_edit_text)
        visaRadio = view.findViewById(R.id.visa_radio)
        cashRadio = view.findViewById(R.id.cash_radio)
        checkoutButton = view.findViewById(R.id.checkout_button)
        backButton = view.findViewById(R.id.back_button)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        checkoutButton.setOnClickListener {
            if (validateInputs()) {
                (activity as MainActivity).replaceFragment(PaymentFragment())
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validar nombre
        val name = nameEditText.text.toString().trim()
        if (name.isEmpty() || name.length < 5 || name.length > 10) {
            nameEditText.error = "El nombre debe tener entre 5 y 10 caracteres"
            isValid = false
        } else {
            nameEditText.error = null
        }

        // Validar teléfono
        val phone = phoneEditText.text.toString().trim()
        if (phone.isEmpty() || phone.length != 10 || !phone.all { it.isDigit() }) {
            phoneEditText.error = "El teléfono debe tener exactamente 10 dígitos"
            isValid = false
        } else {
            phoneEditText.error = null
        }

        // Validar dirección
        val address = addressEditText.text.toString().trim()
        if (address.isEmpty() || address.length < 10 || address.length > 50) {
            addressEditText.error = "La dirección debe tener entre 10 y 50 caracteres"
            isValid = false
        } else {
            addressEditText.error = null
        }

        // Validar método de pago
        if (!visaRadio.isChecked && !cashRadio.isChecked) {
            Toast.makeText(context, "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(context, "Por favor corrige los errores", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }
}