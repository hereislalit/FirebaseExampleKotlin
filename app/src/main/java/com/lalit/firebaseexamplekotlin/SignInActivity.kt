package com.lalit.firebaseexamplekotlin

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private var signInButton: SignInButton? = null
    private var btnLogin: Button? = null
    private var googleApiClient: GoogleApiClient? = null
    private var firbaseAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var tietEmail: TextInputEditText? = null
    private var tietPassword: TextInputEditText? = null
    private var context: Context? = this

    companion object {
        val RC_SIGN_IN = 0
        val LOG_TITLE = "Sign_in_activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        signInButton = findViewById(R.id.sign_in_button) as SignInButton
        signInButton?.setSize(SignInButton.SIZE_STANDARD)
        signInButton?.setOnClickListener(this)
        btnLogin = findViewById(R.id.btn_login) as Button
        btnLogin?.setOnClickListener(this)
        tietEmail = findViewById(R.id.tiet_email) as TextInputEditText
        tietPassword = findViewById(R.id.tiet_password) as TextInputEditText
        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().build()
        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                build()
        firbaseAuth = FirebaseAuth.getInstance()

        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var firebaseUser = p0.currentUser
                if (firebaseUser != null) {
                    Log.d(LOG_TITLE, "onAuthStateChanged sign in: ${firebaseUser.uid}")
                    startActivity(Intent(context, MainActivity().javaClass));
                } else {
                    Log.d(LOG_TITLE, "onAuthStateChanged signout")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firbaseAuth?.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            firbaseAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(LOG_TITLE, "firebaseAuthWithGoogle: ${acct.id}")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firbaseAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        Log.d(LOG_TITLE, "singInWithCredential onComplete: ${task.isSuccessful}")

                        if (!task.isSuccessful) {
                            Log.w(LOG_TITLE, "signInWithCredential: ${task.exception}")
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    override fun onClick(v: View?) {

        when (v) {
            signInButton -> signIn()
            btnLogin -> if (tietEmail!!.text.trim().isNotBlank() && tietPassword!!.text.trim().isNotBlank()) {
                firbaseAuth!!.signInWithEmailAndPassword(tietEmail!!.text.trim().toString(), tietPassword!!.text.toString())
            }
        }
    }

    private fun signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), RC_SIGN_IN)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            var result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(LOG_TITLE, "handleSignInResult: ${result.isSuccess}")
        if (result.isSuccess) {
            var acct: GoogleSignInAccount? = result.signInAccount
            firebaseAuthWithGoogle(acct!!)
            Toast.makeText(context, "Signed in Successfully: ${acct.displayName}", Toast.LENGTH_LONG).show()
        }
    }
}
