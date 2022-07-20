package com.datastructblues.firebaseinstagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.datastructblues.firebaseinstagramclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            intent()
        }

    }
    fun signUpClicked(view: View) {
            email = binding.editTextTextEmailAddress.text.toString()
            password = binding.editTextTextPassword.text.toString()


            // checking the email and password fields are not empty before send an request
            if (!(email.isNotEmpty() && password.isNotEmpty())) {
                Toast.makeText(
                    this, "Either of email or password cannot be empty!",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                //requesti asenkron yapmamız lazım cunku bu işi milisaniyede yapmayabilir ve bu durumda app çoker.
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    //success
                    intent()
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }

        }

        fun signInClicked(view: View) {
            email = binding.editTextTextEmailAddress.text.toString()
            password = binding.editTextTextPassword.text.toString()

            if (!(email.isNotEmpty() && password.isNotEmpty())) {
                Toast.makeText(
                    this,
                    "Either of email or password cannot be empty!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    intent()
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }

        }

        private fun intent() {
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
}
