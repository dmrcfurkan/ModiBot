package com.example.modibot.User.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.modibot.R
import com.example.modibot.User.Payment.PaymentDirections
import com.example.modibot.databinding.FragmentManageProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ManageProfileFragment : Fragment() {
    private var _binding: FragmentManageProfileBinding? = null
    private val binding get() = _binding!!
    var database= FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId)
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val kullaniciAdi =
                        dataSnapshot.child("userName").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val soyisim =
                        dataSnapshot.child("userSurName").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val email=dataSnapshot.child("userEmail").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val password=dataSnapshot.child("userPassword").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    binding.fullName.text = kullaniciAdi+""+soyisim
                    binding.email.text = email
                    binding.nameUpdate.setText(kullaniciAdi)
                    binding.surNameUpdate.setText(soyisim)
                    binding.emailUpdate.setText(email)
                    binding.passwordUpdate.setText(password)

                }
        }
        binding.btnUpdate.setOnClickListener {
            update(it)
        }
        binding.btnDelete.setOnClickListener {
            delete(it)
        }
        binding.btnInactive.setOnClickListener {
            inActive(it)
        }

    }
    fun update(view: View){
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Kullanıcıdan alınan yeni bilgiler
            val newUserName = binding.nameUpdate.text.toString()
            val newUserSurName = binding.surNameUpdate.text.toString()
            val newEmail = binding.emailUpdate.text.toString()
            val newPassword = binding.passwordUpdate.text.toString()

            // 1. Realtime Database Güncelleme
            val userUpdates = mapOf(
                "userName" to newUserName,
                "userSurName" to newUserSurName,
                "userEmail" to newEmail,
                "userPassword" to newPassword
            )

            database.child("users").child(userId)
                .updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Realtime Database güncelleme başarılı
                        println("Realtime Database güncellemesi başarılı.")
                    } else {
                        // Hata oluştu
                        println("Realtime Database güncellemesi başarısız: ${task.exception?.message}")
                    }
                }

            // 2. Firebase Authentication Güncelleme
            val currentUser = FirebaseAuth.getInstance().currentUser

            // Şifre güncelleme
            currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Authentication'da şifre güncellemesi başarılı.")
                    FirebaseAuth.getInstance().signOut()
                    findNavController().navigate(R.id.action_manageProfileFragment_to_login)
                } else {
                    println("Authentication'da şifre güncellemesi başarısız: ${task.exception?.message}")
                }
            }
        } else {
            println("Kullanıcı oturum açmamış.")
        }
    }
    fun delete(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser != null && userId != null) {
            currentUser.delete().addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    // Realtime Database'den de verileri sil
                    database.child("users").child(userId).removeValue()
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.action_manageProfileFragment_to_login)
                            } else {
                                println("Veritabanından silme başarısız: ${dbTask.exception?.message}")
                            }
                        }
                } else {
                    println("Kullanıcı silinemedi: ${deleteTask.exception?.message}")
                }
            }
        }
    }
    fun inActive(view: View){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            // Kullanıcının 'active' parametresini false yapmak
            userRef.child("active").setValue(false)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Realtime Database güncelleme başarılı
                        println("Kullanıcının aktif durumu başarılı bir şekilde false yapıldı.")
                        FirebaseAuth.getInstance().signOut()
                        findNavController().navigate(R.id.action_manageProfileFragment_to_login)
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