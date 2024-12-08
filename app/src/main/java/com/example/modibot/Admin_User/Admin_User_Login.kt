package com.example.modibot.Admin_User

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Path
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.databinding.FragmentAdminUserLoginBinding
import com.example.modibot.databinding.FragmentLoginBinding


class Admin_User_Login : Fragment() {

    private var _binding: FragmentAdminUserLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminUserLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.adminButton.setOnClickListener {
            adminGiris(it)
        }
        binding.userButton.setOnClickListener {
            kullaniciGiris(it)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun adminGiris(view: View){
        val action=Admin_User_LoginDirections.actionAdminUserLoginToAdminLogin()
        Navigation.findNavController(view).navigate(action)
    }
    fun kullaniciGiris(view: View){
        val action=Admin_User_LoginDirections.actionAdminUserLoginToLogin()
        Navigation.findNavController(view).navigate(action)
    }



}