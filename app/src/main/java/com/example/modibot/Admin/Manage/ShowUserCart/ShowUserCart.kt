package com.example.modibot.Admin.Manage.ShowUserCart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.example.modibot.Admin.Manage.UserInfo.User_infoArgs
import com.example.modibot.R
import com.example.modibot.databinding.FragmentShowUserCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.content.ContextCompat


class ShowUserCart : Fragment() {

    private var _binding: FragmentShowUserCartBinding? = null

    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowUserCartBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        arguments?.let {
            val userId = ShowUserCartArgs.fromBundle(it).id
            database.child("users").child(userId).child("cart").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val premiumStatus = snapshot.child("premium").getValue(Boolean::class.java) ?: false
                    val productName = snapshot.child("name").getValue(String::class.java)
                    val productPrice = snapshot.child("price").getValue(String::class.java)

                    if (!productName.isNullOrEmpty() && !productPrice.isNullOrEmpty()) {
                        // Ürünü ekrana yerleştir
                        binding.emptyCartMessage.visibility = View.GONE
                        binding.cartScrollView.visibility = View.VISIBLE

                        binding.itemTitle.text = productName
                        binding.itemPrice.text = "Toplam: $productPrice"

                        if (premiumStatus) {
                            binding.checkoutButton.isEnabled = false
                            binding.checkoutButton.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                        } else {
                            binding.checkoutButton.isEnabled = true
                            //binding.checkoutButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.))
                        }

                    } else {
                        // Sepet boş
                        binding.emptyCartMessage.visibility = View.VISIBLE
                        binding.cartScrollView.visibility = View.GONE
                        binding.checkoutButton.isEnabled = false
                        binding.checkoutButton.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Veri çekilirken hata oluştu.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.checkoutButton.setOnClickListener {
            processPayment()

        }

    }
    private fun processPayment() {
        arguments?.let {
            val userId = ShowUserCartArgs.fromBundle(it).id
            if (userId != null) {
                // Ödeme işlemi başarılı olarak varsayılıyor
                database.child("users").child(userId).child("premium").setValue(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Ödeme tamamlandı, premium aktif edildi!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Ödeme yapıldıktan sonra butonu pasif yap
                            binding.checkoutButton.isEnabled = false
                            binding.checkoutButton.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    android.R.color.darker_gray
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Ödeme işlemi sırasında bir hata oluştu!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Kullanıcı oturum açmamış!", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}