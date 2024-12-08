package com.example.modibot.Admin.Manage.UserInfo

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgument
import com.example.modibot.R
import com.example.modibot.User.Main_Menu.mainMenuArgs
import com.example.modibot.databinding.FragmentUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class User_info : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    //private val args: User_infoArgs by navArgs()
    var database = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val userId = User_infoArgs.fromBundle(it).userId // Burada argümandan doğru userId alınıyor
            println("Seçilen Kullanıcı ID: $userId")

            // Firebase Database'den veriyi çek
            database.child("users").child(userId)
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val userName = dataSnapshot.child("userName").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val userSurName = dataSnapshot.child("userSurName").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val email = dataSnapshot.child("userEmail").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"
                    val password = dataSnapshot.child("userPassword").getValue(String::class.java) ?: "Bilinmeyen Kullanıcı"

                    // UI'da bilgileri göster
                    binding.fullName.text = "$userName $userSurName"
                    binding.email.text = email
                    binding.nameUpdate.setText(userName)
                    binding.surNameUpdate.setText(userSurName)
                    binding.passwordUpdate.setText(password)
                }
                .addOnFailureListener { exception ->
                    println("Veri çekme başarısız: ${exception.message}")
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
        binding.showCart.setOnClickListener {
            showCart(it)
        }
    }

    fun update(view: View){
        arguments?.let {
            // Argümandan gelen userId'yi al
            val userId = User_infoArgs.fromBundle(it).userId

            // Kullanıcıdan alınan yeni bilgiler
            val newUserName = binding.nameUpdate.text.toString()
            val newUserSurName = binding.surNameUpdate.text.toString()
            val newPassword = binding.passwordUpdate.text.toString()

            // Realtime Database Güncelleme
            val userUpdates = mapOf(
                "userName" to newUserName,
                "userSurName" to newUserSurName,
                "userPassword" to newPassword
            )

            database.child("users").child(userId)
                .updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Kullanıcı bilgileri başarıyla güncellendi.")
                    } else {
                        println("Kullanıcı bilgileri güncellenemedi: ${task.exception?.message}")
                    }
                }
        }

        /*if (userId != null) {
            // Kullanıcıdan alınan yeni bilgiler
            val newUserName = binding.nameUpdate.text.toString()
            val newUserSurName = binding.surNameUpdate.text.toString()
            val newPassword = binding.passwordUpdate.text.toString()

            // 1. Realtime Database Güncelleme
            val userUpdates = mapOf(
                "userName" to newUserName,
                "userSurName" to newUserSurName,
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
                    //FirebaseAuth.getInstance().signOut()
                    //findNavController().navigate(R.id.action_manageProfileFragment_to_login)
                } else {
                    println("Authentication'da şifre güncellemesi başarısız: ${task.exception?.message}")
                }
            }
        } else {
            println("Kullanıcı oturum açmamış.")
        }*/
    }
    fun delete(view: View) {
        arguments?.let {
            // Argümandan gelen userId'yi al
            val userId = User_infoArgs.fromBundle(it).userId

            // Firebase Database'den kullanıcıyı sil
            database.child("users").child(userId).removeValue()
                .addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        println("Kullanıcı veritabanından başarıyla silindi.")
                    } else {
                        println("Kullanıcı veritabanından silinemedi: ${dbTask.exception?.message}")
                    }
                }
        }
        /*val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser != null && userId != null) {
            currentUser.delete().addOnCompleteListener { deleteTask ->
                if (deleteTask.isSuccessful) {
                    // Realtime Database'den de verileri sil
                    database.child("users").child(userId).removeValue()
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                //FirebaseAuth.getInstance().signOut()
                                //findNavController().navigate(R.id.action_manageProfileFragment_to_login)
                            } else {
                                println("Veritabanından silme başarısız: ${dbTask.exception?.message}")
                            }
                        }
                } else {
                    println("Kullanıcı silinemedi: ${deleteTask.exception?.message}")
                }
            }
        }*/
    }
    fun inActive(view: View){
        arguments?.let {
            // Argümandan gelen userId'yi al
            val userId = User_infoArgs.fromBundle(it).userId

            // Kullanıcının 'active' parametresini false yapmak
            database.child("users").child(userId).child("active")
                .setValue(false)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Kullanıcı başarıyla inaktif yapıldı.")
                    } else {
                        println("Kullanıcı inaktif yapılamadı: ${task.exception?.message}")
                    }
                }
        }
        /*val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            // Kullanıcının 'active' parametresini false yapmak
            userRef.child("active").setValue(false)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Realtime Database güncelleme başarılı
                        println("Kullanıcının aktif durumu başarılı bir şekilde false yapıldı.")
                        //FirebaseAuth.getInstance().signOut()
                        //findNavController().navigate(R.id.action_manageProfileFragment_to_login)
                    } else {
                        // Hata oluştu
                        println("Kullanıcının aktif durumu false yaparken hata oluştu: ${task.exception?.message}")
                    }
                }
        }*/
    }
    fun showCart(view: View){
        arguments?.let {
            val userId = User_infoArgs.fromBundle(it).userId
            val action=User_infoDirections.actionUserInfoToShowUserCart(userId)
            Navigation.findNavController(view).navigate(action)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    }

