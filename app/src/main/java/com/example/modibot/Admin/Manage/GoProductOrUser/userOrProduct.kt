package com.example.modibot.Admin.Manage.GoProductOrUser

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.databinding.FragmentUserOrProductBinding


class userOrProduct : Fragment() {

    private var _binding: FragmentUserOrProductBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnManageUsers.setOnClickListener {
            val action=userOrProductDirections.actionUserOrProductToManageUser()
            Navigation.findNavController(view).navigate(action)
        }
        binding.btnManageProducts.setOnClickListener {
            val action=userOrProductDirections.actionUserOrProductToManageProduct()
            Navigation.findNavController(view).navigate(action)
        }
        binding.adminInfo.setOnClickListener {
            val action=userOrProductDirections.actionUserOrProductToAdminUpdateInfo()
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserOrProductBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


}