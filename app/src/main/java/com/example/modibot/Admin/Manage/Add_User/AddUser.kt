package com.example.modibot.Admin.Manage.Add_User

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.modibot.R
import com.example.modibot.User.Sign_in.SignFragmentDirections
import com.example.modibot.database.UserSign
import com.example.modibot.databinding.FragmentAddUserBinding
import com.example.modibot.databinding.FragmentSignBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddUser : Fragment() {
    var database=FirebaseDatabase.getInstance().reference
    val mAuth= FirebaseAuth.getInstance()
    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signButton.setOnClickListener {
            addUser(it)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun addUser(view: View) {
        if (_binding == null || !isAdded) {
            return
        }
        val userName = binding.nameInput.text.toString()
        val userSurName = binding.surNameInput.text.toString()
        val userEmail = binding.emailInput.text.toString()
        val userPassword = binding.passwordInput.text.toString()
        val user = UserSign(userName, userSurName, userEmail, userPassword, isPremium =false, isActive =true,"")

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(requireContext(), "Email Alanı Boş Bırakılamaz!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(requireContext(), "Şifre Alanı Boş Bırakılamaz!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(requireContext(), "İsim Alanı Boş Bırakılamaz!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(userSurName)) {
            Toast.makeText(requireContext(), "Soyisim Alanı Boş Bırakılamaz!", Toast.LENGTH_SHORT).show()
        } else {
            // Kullanıcı bilgilerini UserSign objesi ile oluşturuyorsunuz
            val user = UserSign(userName, userSurName, userEmail, userPassword,isPremium = false, isActive = true,"")

            // Kullanıcı kaydını başlatıyoruz
            register(userEmail, userPassword,user,view)
        }
    }

    fun register(userEmail: String, userPassword: String, user: UserSign,view: View) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kullanıcı bilgilerini veritabanına kaydetme
                    val userId = mAuth.currentUser?.uid
                    val userRef = database.child("users").child(userId!!)
                    userRef.setValue(user).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Kullanıcı Başarıyla Kaydedildi!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val action = AddUserDirections.actionAddUserToManageUser()
                            Navigation.findNavController(view).navigate(action)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Veritabanına Kaydetme Hatası: ${dbTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Kayıt Hatası: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}