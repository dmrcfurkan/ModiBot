package com.example.modibot.User.Payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.modibot.R
import com.example.modibot.databinding.FragmentPaymentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class Payment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase Database Referansı
        database = FirebaseDatabase.getInstance().reference

        val etCardNumber: EditText = binding.etCardNumber
        val etExpirationDate: EditText = binding.etExpirationDate
        val etCvv: EditText = binding.etCvv
        val cardLogo: ImageView = binding.cardLogo
        val expirationDateText: TextView = binding.expirationDateText  // Son kullanma tarihi TextView'i

        // Kart Numarası Dinleyicisi
        etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var cardNumber = s.toString().replace(" ", "") // Boşlukları kaldır

                // Kart numarasına her 4 hanede bir boşluk ekle
                val formattedCardNumber = StringBuilder()
                for (i in cardNumber.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formattedCardNumber.append(" ")
                    }
                    formattedCardNumber.append(cardNumber[i])
                }

                // Kart numarasını güncelle
                binding.cardNumberText.text = formattedCardNumber.toString()
                updateCardLogo(cardNumber)

                // Luhn algoritması ile kontrol
                if (cardNumber.length >= 13) { // En az 13 haneli kartlar kontrol edilir
                    if (isValidCardNumber(cardNumber)) {
                        etCardNumber.error = null
                    } else {
                        etCardNumber.error = "Geçersiz kart numarası"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Son Kullanma Tarihini TextView'e güncelle
        etExpirationDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.length == 2 && start != 0) {
                    val formatted = "$input/"
                    etExpirationDate.setText(formatted)
                    etExpirationDate.setSelection(formatted.length)
                } else if (input.length > 5) {
                    etExpirationDate.setText(input.substring(0, 5))
                    etExpirationDate.setSelection(5)
                }

                if (input.length == 5) {
                    val parts = input.split("/")
                    if (parts.size == 2) {
                        val month = parts[0].toIntOrNull()
                        val year = parts[1].toIntOrNull()
                        if (month != null && year != null && (month !in 1..12 || year < Calendar.getInstance().get(Calendar.YEAR) % 100)) {
                            etExpirationDate.error = "Geçersiz tarih"
                        } else {
                            etExpirationDate.error = null
                        }
                        expirationDateText.text=(s.toString())
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // Ödeme butonu tıklama
        binding.button4.setOnClickListener {
            processPayment()
        }

        return view
    }

    private fun processPayment() {
        val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
        val expirationDate = binding.etExpirationDate.text.toString()

        if (!isValidCardNumber(cardNumber)) {
            Toast.makeText(context, "Geçersiz kart numarası! Lütfen doğru bir numara girin.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidExpirationDate(expirationDate)) {
            Toast.makeText(context, "Geçersiz son kullanma tarihi! Lütfen doğru bir tarih girin.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("premium").setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Premium aktif edildi!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Premium aktif edilirken bir hata oluştu!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Luhn Algoritması
    private fun isValidCardNumber(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            val n = cardNumber[i].toString().toIntOrNull() ?: return false
            if (alternate) {
                val doubled = n * 2
                sum += if (doubled > 9) doubled - 9 else doubled
            } else {
                sum += n
            }
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    // Son Kullanma Tarihi Geçerlilik Kontrolü
    private fun isValidExpirationDate(expirationDate: String): Boolean {
        try {
            // Son kullanma tarihi formatı "MM/YY" olmalı
            val parts = expirationDate.split("/")
            if (parts.size != 2) return false

            val month = parts[0].toIntOrNull() ?: return false
            val year = parts[1].toIntOrNull() ?: return false

            // Geçerli bir ay (1-12 arasında olmalı)
            if (month !in 1..12) return false

            // Bugünün tarihini al
            val currentCalendar = Calendar.getInstance()
            val currentMonth = currentCalendar.get(Calendar.MONTH) + 1 // Aylar 0'dan başlar
            val currentYear = currentCalendar.get(Calendar.YEAR) % 100  // Yılın son iki hanesi

            // Geçerli bir tarih olup olmadığını kontrol et
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                return false
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    // Kart Logosu Güncelleme
    private fun updateCardLogo(cardNumber: String) {
        when {
            cardNumber.startsWith("4") -> {
                binding.cardLogo.setImageResource(R.drawable.visa_card)
                binding.cardLogo.visibility = View.VISIBLE
            }
            cardNumber.startsWith("5") -> {
                binding.cardLogo.setImageResource(R.drawable.mastercard_logo)
                binding.cardLogo.visibility = View.VISIBLE
            }
            cardNumber.startsWith("3") -> {
                binding.cardLogo.setImageResource(R.drawable.amex_logo)
                binding.cardLogo.visibility = View.VISIBLE
            }
            cardNumber.startsWith("6") -> {
                binding.cardLogo.setImageResource(R.drawable.discover_logo)
                binding.cardLogo.visibility = View.VISIBLE
            }
            cardNumber.isEmpty() -> {
                binding.cardLogo.visibility = View.GONE
            }
            else -> {
                //binding.cardLogo.setImageResource(R.drawable.default_logo)
                binding.cardLogo.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}