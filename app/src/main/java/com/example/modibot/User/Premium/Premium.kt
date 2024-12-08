package com.example.modibot.User.Premium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.User.Main_Menu.mainMenuDirections
import com.example.modibot.databinding.FragmentPremiumBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class Premium : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        checkLimitedPremiumAvailability()

        // Premium Limited seçeneği
        binding.tryLimitedButton.setOnClickListener {
            val userId = firebaseAuth.currentUser?.uid ?: return@setOnClickListener
            // Firebase'den fiyatı alıp sepete ekle
            firebaseDatabase.reference.child("premiumProducts").child("limited").child("price")
                .get()
                .addOnSuccessListener { snapshot ->
                    val price = "₺${snapshot.value.toString()}"

                    // Ürün adını güncelleme
                    firebaseDatabase.reference.child("premiumProducts").child("limited").child("name")
                        .get()
                        .addOnSuccessListener { nameSnapshot ->
                            val name = nameSnapshot.value.toString() // Ürün adını al
                            addToCart(userId, name, price)
                            decreaseStock("limited")

                            val action = PremiumDirections.actionPremiumToCartFragment2(name, price)
                            Navigation.findNavController(view).navigate(action)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Ad alınırken hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Fiyat alınırken hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }


        binding.tryButton.setOnClickListener {
            val userId = firebaseAuth.currentUser?.uid ?: return@setOnClickListener
            addToCart(userId, "Premium Normal", "₺29,99")

            val action = PremiumDirections.actionPremiumToCartFragment2("Premium Normal", "₺29,99")
            Navigation.findNavController(view).navigate(action)
        }

        binding.tryExtraButton.setOnClickListener {
            val userId = firebaseAuth.currentUser?.uid ?: return@setOnClickListener
            addToCart(userId, "Premium Extra", "₺49,99")

            val action = PremiumDirections.actionPremiumToCartFragment2("Premium Extra", "₺49,99")
            Navigation.findNavController(view).navigate(action)
        }
    }
    private fun addToCart(userId: String, name: String, price: String) {
        val cartRef = firebaseDatabase.reference.child("users").child(userId).child("cart")

        val cartItem = mapOf(
            "name" to name,
            "price" to price
        )

        cartRef.setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(context, "Ürün sepete eklendi!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Sepete ekleme sırasında hata oluştu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun decreaseStock(productId: String) {
        val productRef = firebaseDatabase.reference.child("premiumProducts").child("limited")
        productRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentStock = mutableData.child("stock").getValue(Long::class.java) ?: return Transaction.success(mutableData)
                if (currentStock > 0) {
                    mutableData.child("stock").value = currentStock - 1 // Stok bir azalt
                    return Transaction.success(mutableData)
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed) {
                    Toast.makeText(context, "Stok güncellendi.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Stok güncellenemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun checkLimitedPremiumAvailability() {
        // Firebase'den limited premium bilgilerini kontrol et
        firebaseDatabase.reference.child("premiumProducts").child("limited")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val isAvailable = snapshot.child("isAvailable").value as? Boolean ?: false
                    val stock = snapshot.child("stock").value as? Long ?: 0L
                    val price = snapshot.child("price").value as? Long ?: 0.0
                    val name=snapshot.child("name").value as? String ?:"Ürün adı bulunamadı"

                    // Fiyatı güncelle
                    val formattedPrice = "₺$price"
                    binding.subLimitedTitle.text ="Sadece "+ formattedPrice
                    binding.premiumLimitedTitle.text=name

                    if (!isAvailable || stock <= 0) {
                        // Eğer ürün uygun değilse görünümü gizle
                        binding.premiumLimited.visibility = View.GONE
                        Toast.makeText(context, "Premium Limited şu anda mevcut değil.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Ürün uygunsa görünümü aktif tut
                        binding.premiumLimited.visibility = View.VISIBLE
                    }
                } else {
                    // Eğer veriler yoksa varsayılan olarak limited'i gizle
                    binding.premiumLimited.visibility = View.GONE
                    Toast.makeText(context, "Premium Limited bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.premiumLimited.visibility = View.GONE
                Toast.makeText(context, "Premium Limited kontrolü sırasında hata oluştu.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}