package com.example.modibot.User.Login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.modibot.database.UserSign
import com.example.modibot.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    val mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.kayitOlLink.setOnClickListener {
            kayitOl(it)
        }

        binding.loginButton.setOnClickListener {
            login(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun kayitOl(view: View) {
        val action = LoginDirections.actionLoginToSignFragment()
        Navigation.findNavController(view).navigate(action)
    }

    fun login(view: View) {
        val userEmail = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        if (userEmail!="admin@gmail.com") {

            if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(password)) {
                Toast.makeText(
                    requireContext(),
                    "Lütfen Bilgilerinizi Kontrol Ediniz",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mAuth.signInWithEmailAndPassword(userEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Giriş Başarılı", Toast.LENGTH_SHORT)
                                .show()

                            // Kullanıcı bilgilerini almak için veritabanından çekiyoruz
                            val userId = mAuth.currentUser?.uid
                            val userRef = database.child("users").child(userId!!)

                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val isActive =
                                        dataSnapshot.child("active").getValue(Boolean::class.java)
                                            ?: false

                                    if (isActive) {
                                        // Kullanıcı aktif, MainMenu'ya yönlendiriyoruz
                                        val userName = dataSnapshot.child("userName")
                                            .getValue(String::class.java)
                                        val userSurName = dataSnapshot.child("surName")
                                            .getValue(String::class.java)
                                        val action = LoginDirections.actionLoginToMainMenu(
                                            userName ?: "Bilinmiyor"
                                        )
                                        Navigation.findNavController(view).navigate(action)
                                    } else {
                                        // Kullanıcı pasif, Inactive ekranına yönlendiriyoruz
                                        val action = LoginDirections.actionLoginToInactive()
                                        Navigation.findNavController(view).navigate(action)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Veri alınırken hata oluştu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Giriş Sırasında Bir Hata Oluştu",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        }
        else{
            Toast.makeText(requireContext(), "Bu hesap kullanılamaz", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
