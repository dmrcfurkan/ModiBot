package com.example.modibot.Admin.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.databinding.FragmentAdminLoginBinding
import com.example.modibot.databinding.FragmentMainMenuBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log


class AdminLogin : Fragment() {

    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            login(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun login(view: View) {
        val getEmail = binding.emailInput.text.toString()
        val getPassword = binding.passwordInput.text.toString()

        // Firebase ile giriş yap
        FirebaseAuth.getInstance().signInWithEmailAndPassword(getEmail, getPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Giriş başarılı olduğunda, admin'e özel yönlendirme yap
                    if (getEmail == "admin@gmail.com") {
                        val action = AdminLoginDirections.actionAdminLoginToUserOrProduct()
                        Navigation.findNavController(view).navigate(action)
                        Toast.makeText(requireContext(), "Giriş Başarılı", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Admin olmayan bir kullanıcı",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Eğer giriş başarısızsa hata mesajını logla
                    val exception = task.exception?.localizedMessage
                    Toast.makeText(requireContext(), "Hatalı giriş: $exception", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("LoginError", exception ?: "Unknown error")
                }
            }
    }
}