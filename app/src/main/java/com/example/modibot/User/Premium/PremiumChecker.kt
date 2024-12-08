package com.example.modibot.User.Premium

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PremiumChecker {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun checkPremiumStatus(onResult: (Boolean) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            databaseReference.child(userId).child("premium").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isPremium = snapshot.getValue(Boolean::class.java) ?: false
                    onResult(isPremium)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false) // Hata durumunda premium olarak kabul etmiyoruz.
                }
            })
        } else {
            onResult(false) // Kullanıcı oturum açmamışsa premium değil.
        }
    }
}