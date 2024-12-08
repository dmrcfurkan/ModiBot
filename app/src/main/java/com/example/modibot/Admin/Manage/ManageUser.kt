package com.example.modibot.Admin.Manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modibot.R
import com.example.modibot.database.UserSign
import com.example.modibot.databinding.FragmentManageUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ManageUser : Fragment() {
    var database=FirebaseDatabase.getInstance().reference
    private var _binding: FragmentManageUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return
        binding.floatingActionButton.setOnClickListener {
            addUser(it)
        }

        val getData= object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val sb=ArrayList<String>()
                val userList = ArrayList<UserSign>()
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                for (i in snapshot.children){
                    val ad = i.child("userName").getValue(String::class.java) ?: "Ad Yok"
                    val soyad = i.child("userSurName").getValue(String::class.java) ?: "Soyad Yok"
                    val email = i.child("userEmail").getValue(String::class.java) ?: "Email Yok"
                    val sifre = i.child("userPassword").getValue(String::class.java) ?: "Şifre Yok"
                    val isPremium = i.child("premium").getValue(Boolean::class.java) ?: false
                    val isActive = i.child("active").getValue(Boolean::class.java) ?: true
                    val userIdFromDb = i.key ?: ""

                    // Dizeyi oluşturup listeye ekleyelim
                    val userSign = UserSign(ad, soyad, email, sifre,isPremium, isActive ,userIdFromDb)
                    userList.add(userSign)
                    sb.add(" $ad $soyad \n $email")

                }
                val adapter=UserAdapter(sb,userList)
                adapter.setOnItemClickListener((object : UserAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val selectedUser = userList[position]
                        val action=ManageUserDirections.actionManageUserToUserInfo(selectedUser.userId)
                        Navigation.findNavController(view).navigate(action)
                    }
                }))
                binding.userListRecyclerView.layoutManager=LinearLayoutManager(requireContext());
                binding.userListRecyclerView.adapter=adapter


            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.child("users").addListenerForSingleValueEvent(getData)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addUser(view: View){
        val action=ManageUserDirections.actionManageUserToAddUser();
        Navigation.findNavController(view).navigate(action)
    }

}






