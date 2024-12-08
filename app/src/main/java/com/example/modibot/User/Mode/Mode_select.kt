package com.example.modibot.User.Mode

import android.os.Bundle
import android.text.Layout.Directions
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.modibot.Services.ApiKeyInterceptor
import com.example.modibot.Services.Content
import com.example.modibot.Services.GeminiApiService
import retrofit2.Retrofit
import com.example.modibot.Services.Part
import com.example.modibot.Services.RequestBody
import com.example.modibot.Services.RetrofitClient
import com.example.modibot.Services.YourResponseModel
import com.example.modibot.databinding.FragmentModeSelectBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory


class Mode_select : Fragment() {

    private var _binding: FragmentModeSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModeSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageHappy = "Öneri ver: Mutlu ruh halindeki bir kişi için birkaç aktivite öner."
        val messageSad = "Öneri ver: Üzgün ruh halindeki bir kişi için birkaç aktivite öner."
        val messageTired = "Öneri ver: Yorgun ruh halindeki bir kişi için birkaç aktivite öner."
        val messageEnergy = "Öneri ver: Enerjik ruh halindeki bir kişi için birkaç aktivite öner."

        binding.btnHappy.setOnClickListener {
            if (messageHappy.isNotEmpty()) {
                sendMessageToGemini(view, messageHappy)
            }
        }

        binding.btnSad.setOnClickListener {
            if (messageSad.isNotEmpty()) {
                sendMessageToGemini(view, messageSad)
            }
        }

        binding.btnTired.setOnClickListener {
            if (messageTired.isNotEmpty()) {
                sendMessageToGemini(view, messageTired)
            }
        }

        binding.btnEnergy.setOnClickListener {
            if (messageEnergy.isNotEmpty()) {
                sendMessageToGemini(view, messageEnergy)
            }
        }
    }

    private fun sendMessageToGemini(view: View, message: String) {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyAIm0zwsRb4wvZLuTPNYj0cnyek038QYp4"
        )
        val prompt = message
        MainScope().launch {
            try {
                val response = generativeModel.generateContent(prompt)
                if (response != null && response.text.toString().isNotEmpty()) {
                    val action = Mode_selectDirections.actionModeSelectToSuggestion(response.text ?: "Bulunamadı")
                    Navigation.findNavController(view).navigate(action)
                    println(response.text)

                    // Başarılı yanıt sonrası diğer fragment'a geçiş

                }
            } catch (e: Exception) {
                Log.e("Mode_select", "Error while generating content: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

