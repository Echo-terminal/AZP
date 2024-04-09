package com.example.azp.utilities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT:DatabaseReference

const val NODE_USER = "users"
const val CHILD_UID = "id"
const val CHILD_USERNAME = "username"
const val CHILD_EMAIL = "email"
fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
}