package com.example.modibot.User.Premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.User.Main_Menu.mainMenuArgs
import com.example.modibot.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartItems: Array<PremiumOption> // Sepet verisi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val premiumType= CartFragmentArgs.fromBundle(it).uyelik
            val price= CartFragmentArgs.fromBundle(it).fiyat
            binding.itemTitle.text = premiumType
            binding.itemPrice.text = price
            binding.totalPrice.text=price
        }
        binding.checkoutButton.setOnClickListener {
            val action=CartFragmentDirections.actionCartFragment2ToPayment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.clearCart.setOnClickListener {
            clearCart()
            val action=CartFragmentDirections.actionCartFragment2ToPremium()
            Navigation.findNavController(view).navigate(action)
        }
    }
    private fun clearCart() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(user.uid)

            // Sepeti temizle
            userRef.child("cart").removeValue()
                .addOnSuccessListener {
                    // Başarıyla silindiğinde yapılacaklar
                    // Mesaj gösterebilirsin
                    println("Sepet başarıyla silindi")
                }
                .addOnFailureListener { exception ->
                    // Hata durumunda yapılacaklar
                    println("Sepet silinirken bir hata oluştu: ${exception.message}")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

