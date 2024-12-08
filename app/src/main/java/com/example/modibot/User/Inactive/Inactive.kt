package com.example.modibot.User.Inactive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.modibot.R
import com.example.modibot.databinding.FragmentInactiveBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class Inactive : Fragment() {

    private var _binding: FragmentInactiveBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInactiveBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.active.setOnClickListener {
            setActive(it)
        }
    }
    fun setActive(view: View){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            // Kullanıcının 'active' parametresini false yapmak
            userRef.child("active").setValue(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Realtime Database güncelleme başarılı
                        println("Kullanıcının aktif durumu başarılı bir şekilde true yapıldı.")
                        FirebaseAuth.getInstance().signOut()
                        findNavController().navigate(R.id.action_inactive_to_login)
                    } else {
                        // Hata oluştu
                        println("Kullanıcının aktif durumu false yaparken hata oluştu: ${task.exception?.message}")
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}