package com.example.modibot.User.Smart_Assistance

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.unit.Constraints.Companion.Infinity
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.User.Mode.Mode_selectDirections
import com.example.modibot.databinding.FragmentSmartAssistanceBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SmartAssistance : Fragment() {

    private var _binding: FragmentSmartAssistanceBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmartAssistanceBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val message=binding.editTextText.text.toString()
            if (message.isNotEmpty()){
                sendMessageToGemini(view,message)


            }
        }
    }

    private fun sendMessageToGemini(view: View, message: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = firebaseDatabase.getReference("users").child(userId)

            // Kullanıcı verilerini çekiyoruz
            userRef.get().addOnSuccessListener { snapshot ->
                // Veritabanından gelen kullanıcı verilerini alıyoruz
                val isActive = snapshot.child("active").getValue(Boolean::class.java) ?: false
                val isPremium = snapshot.child("premium").getValue(Boolean::class.java) ?: false
                val cartName = snapshot.child("cart").child("name").getValue(String::class.java) ?: ""
                var remainingQuestions = snapshot.child("cart").child("remainingQuestions").getValue(Int::class.java) ?: 0

                // Eğer first-time user veya remainingQuestions 0 ise, default değer ver
                if (remainingQuestions == 0) {
                    remainingQuestions = when (cartName) {
                        "Premium Normal" -> 10
                        "Premium Extra" -> 20
                        "Premium Limited" -> Infinity
                        else -> 0 // Premium değilse hak yok
                    }
                    // Yeni kullanıcı veya 0 ise, remainingQuestions değerini güncelliyoruz
                    userRef.child("cart").child("remainingQuestions").setValue(remainingQuestions)
                }

                // Kullanıcı aktif ve premium ise ve hakları varsa, işlem yapılır
                if (isActive && isPremium && remainingQuestions > 0) {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = "AIzaSyAIm0zwsRb4wvZLuTPNYj0cnyek038QYp4"
                    )
                    val prompt = message
                    MainScope().launch {
                        try {
                            val response = generativeModel.generateContent(prompt)
                            if (response != null && response.text.toString().isNotEmpty()) {
                                binding.textView10.text = response.text
                                println(response.text)

                                // Kullanıcının kalan hakkını bir azalt
                                val newRemaining = remainingQuestions - 1
                                userRef.child("cart").child("remainingQuestions").setValue(newRemaining)

                                // Eğer hak biterse premium'u kapat
                                if (newRemaining == 0) {
                                    userRef.child("premium").setValue(false)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SmartAssistance", "Error while generating content: ${e.message}")
                        }
                    }
                } else {
                    binding.textView10.text = "Soru sorma hakkınız kalmadı. Premium üyelik alarak hakkınızı yenileyebilirsiniz."
                }
            }.addOnFailureListener { exception ->
                Log.e("SmartAssistance", "Error fetching user data: ${exception.message}")
            }
        } else {
            binding.textView10.text = "Oturum açılmadı. Lütfen giriş yapın."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}