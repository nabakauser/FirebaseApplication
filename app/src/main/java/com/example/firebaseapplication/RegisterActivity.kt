package com.example.firebaseapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.firebaseapplication.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var bind: ActivityRegisterBinding? = null
    private var number: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind?.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        bind?.uiBtnRegister?.setOnClickListener {
            registerUser()
        }
        bind?.uiTvGoToLogin?.setOnClickListener {
            navigateToLoginPage()
        }
        bind?.uiBtnSendOTP?.setOnClickListener {
            sendOtp()
        }
    }

    private fun registerUser() {
        val userEmail = bind?.uiEtEmail?.text.toString()
        val userPassword = bind?.uiEtPassword?.text.toString()

        auth.createUserWithEmailAndPassword(userEmail, userPassword)?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }?.addOnFailureListener { exception ->
           Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun sendOtp() {
        val userNumber = bind?.uiEtPhoneNumber?.text.toString()
        if(userNumber.isNotEmpty()) {
            if(userNumber.length == 10) {
                number = "+91$userNumber"

                val authOptions = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(userNumber)
                    .setTimeout(20, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(authOptions)
            } else {
                Toast.makeText(this,"Wrong phone number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,"Enter phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            Log.d("TAG", "onVerificationFailed $exception")

            if (exception is FirebaseAuthInvalidCredentialsException) {
                Log.d("TAG", "onVerificationFailed $exception")
            } else
                if (exception is FirebaseTooManyRequestsException) {
                    Log.w("TAG", "onVerificationFailed $exception")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            val intent = Intent(this@RegisterActivity, OtpActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", verificationId)
            startActivity(intent)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")


                    val user = task.result?.user
                } else {
                    Log.d("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
            }
    }
}

