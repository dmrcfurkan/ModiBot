package com.example.modibot.Admin.Manage.AdminUpdateInfo

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.modibot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AdminUpdateInfo : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var passwordInput: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Firebase Auth instance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_update_info, container, false)

        passwordInput = view.findViewById(R.id.passwordInput)
        updateButton = view.findViewById(R.id.loginButton)

        updateButton.setOnClickListener {
            val newPassword = passwordInput.text.toString().trim()
            if (TextUtils.isEmpty(newPassword) || newPassword.length < 6) {
                Toast.makeText(requireContext(), "Şifre en az 6 karakter olmalı!", Toast.LENGTH_SHORT).show()
            } else {
                updatePassword(newPassword)
            }
        }

        return view
    }

    private fun updatePassword(newPassword: String) {
        val user: FirebaseUser? = auth.currentUser
        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Şifre güncellendi
                        Toast.makeText(requireContext(), "Şifre başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                        val action = AdminUpdateInfoDirections.actionAdminUpdateInfoToAdminLogin()
                        view?.let {
                            Navigation.findNavController(it).navigate(action)
                        }

                        // Firebase Realtime Database'i güncelle (isteğe bağlı)
                        val adminUid = it.uid
                        val databaseRef = FirebaseDatabase.getInstance().getReference("admin")
                        databaseRef.child(adminUid).child("password").setValue(newPassword)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Veritabanı güncellendi.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "Veritabanı güncellenemedi.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "Şifre güncellenemedi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } ?: run {
            Toast.makeText(requireContext(), "Kullanıcı doğrulanamadı, lütfen yeniden giriş yapın.", Toast.LENGTH_SHORT).show()
        }
    }
}
