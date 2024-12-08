package com.example.modibot.User.Suggestion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.modibot.R
import com.example.modibot.User.Main_Menu.mainMenuArgs
import com.example.modibot.databinding.FragmentSuggestionBinding


class Suggestion : Fragment() {

    private var _binding: FragmentSuggestionBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val suggestion= SuggestionArgs.fromBundle(it).suggestion
            binding.textView2.text=suggestion
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


