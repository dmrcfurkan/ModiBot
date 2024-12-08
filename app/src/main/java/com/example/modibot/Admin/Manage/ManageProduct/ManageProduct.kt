package com.example.modibot.Admin.Manage.ManageProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.modibot.R
import com.example.modibot.databinding.FragmentManageProductBinding
import com.google.firebase.database.FirebaseDatabase

class ManageProduct : Fragment() {

    private var _binding: FragmentManageProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCurrentStock()

        // Stok Güncelle
        binding.updateStockButton.setOnClickListener {
            val newStock = binding.stockInput.text.toString().toIntOrNull()
            if (newStock != null) {
                updateProductField("limited", "stock", newStock)
            } else {
                Toast.makeText(context, "Geçerli bir stok girin", Toast.LENGTH_SHORT).show()
            }
        }

        // Fiyat Güncelle
        binding.updatePriceButton.setOnClickListener {
            val newPrice = binding.priceInput.text.toString().toDoubleOrNull()
            if (newPrice != null) {
                updateProductField("limited", "price", newPrice)
            } else {
                Toast.makeText(context, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
            }
        }
        binding.updateNameButton.setOnClickListener {
            val newName = binding.nameInput.text.toString()
            if (newName.isNotBlank()) {
                updateProductField("limited", "name", newName) // "limited" ürünü, "name" alanı güncelleniyor
            } else {
                Toast.makeText(context, "Geçerli bir ürün adı girin", Toast.LENGTH_SHORT).show()
            }
        }


        // Satış Durumu Güncelle
        binding.updateAvailabilityButton.setOnClickListener {
            val isAvailable = binding.availableSwitch.isChecked
            updateProductField("limited", "isAvailable", isAvailable)
        }

        // Soru Limiti Güncelle
    }

    private fun updateProductField(productId: String, field: String, value: Any) {
        val database = FirebaseDatabase.getInstance().getReference("premiumProducts/$productId")
        database.child(field).setValue(value).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Güncelleme başarılı: $field = $value", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Hata: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun loadCurrentStock() {
        val database = FirebaseDatabase.getInstance().getReference("premiumProducts/limited/stock")
        database.get().addOnSuccessListener { snapshot ->
            val currentStock = snapshot.getValue(Int::class.java) ?: 0 // Eğer değer boşsa 0 döndür
            binding.textView7.text = "Mevcut Stok: $currentStock"
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Stok bilgisi alınamadı: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
