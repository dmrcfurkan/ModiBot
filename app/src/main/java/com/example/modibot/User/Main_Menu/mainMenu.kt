package com.example.modibot.User.Main_Menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.User.Premium.PremiumChecker
import com.example.modibot.databinding.FragmentMainMenuBinding


class mainMenu : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
           val isim= mainMenuArgs.fromBundle(it).kullaniciAdi
            binding.welcomeText.text="Merhaba "+isim+", Ne Yapmak İstersin?"
        }
        binding.activityCard.setOnClickListener {
            modeSelect(it);
        }
        binding.assistantCard.setOnClickListener {
            val premiumChecker = PremiumChecker()
            premiumChecker.checkPremiumStatus { isPremium ->
                if (isPremium) {
                    goAssistant(it);
                }
                else{
                    Toast.makeText(context, "Öde!", Toast.LENGTH_SHORT).show()
                    val action=mainMenuDirections.actionMainMenuToPremium()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
        binding.profileCard.setOnClickListener {
            goManageProfile(it)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun modeSelect(view: View){
        val action=mainMenuDirections.actionMainMenuToModeSelect()
        Navigation.findNavController(view).navigate(action)
    }
    fun goAssistant(view: View){
        val action=mainMenuDirections.actionMainMenuToSmartAssistance()
        Navigation.findNavController(view).navigate(action)
    }
    fun goManageProfile(view: View){
        val action=mainMenuDirections.actionMainMenuToManageProfileFragment()
        Navigation.findNavController(view).navigate(action)
    }
}