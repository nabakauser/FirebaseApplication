package com.example.firebaseapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class LoginActivity : AppCompatActivity() {
    private val auth: FirebaseAuth? by lazy {
        FirebaseAuth.getInstance()
    }
    private val xx: PhoneAuthProvider? by lazy {
        PhoneAuthProvider.getInstance()
    }
    private var bind: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind?.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        bind?.uiBtnLogin?.setOnClickListener {
            loginUser()
        }
        bind?.uiTvGoToRegister?.setOnClickListener {
            navigateToRegisterPage()
        }
    }

    private fun loginUser() {
        val userEmail = bind?.uiEtEmail?.text.toString()
        val userPassword = bind?.uiEtPassword?.text.toString()
//        xx.verifyPhoneNumber()
        auth?.signInWithEmailAndPassword(userEmail, userPassword)?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }?.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToRegisterPage() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}