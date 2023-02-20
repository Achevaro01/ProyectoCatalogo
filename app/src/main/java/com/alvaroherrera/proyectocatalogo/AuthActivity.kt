package com.alvaroherrera.proyectocatalogo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alvaroherrera.proyectocatalogo.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class AuthActivity : AppCompatActivity() {
        private val GOOGLE_SIGN_IN = 100
        private lateinit var binding: ActivityAuthBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAuthBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //Anaytics Event
            val analytics: FirebaseAnalytics =  FirebaseAnalytics.getInstance(this)
            val bundle = Bundle()
            bundle.putString("message", "integración de Firebase completa")
            analytics.logEvent("InitScreen",bundle)

            //Setup()
            setup()
            session()

        }

        override fun onStart() {
            super.onStart()

            binding.authLayout.visibility = View.VISIBLE
        }

        private fun session() {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            val email = prefs.getString("email", null)
            val provider = prefs.getString("provider", null)

            if(email != null && provider != null) {
                binding.authLayout.visibility = View.INVISIBLE
                showHome(email, ProviderType.valueOf(provider))
            }

        }

        private fun setup() {
            title = "Autenticación"
            binding.signUpButton.setOnClickListener {
                if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
                }
            }

            binding.loginButton.setOnClickListener {
                if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
                }
            }
            binding.googleButton.setOnClickListener {

                // Configuracion
                val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("336456495480-52s0bfld91l61365tt5s6otknc22ac0g.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient(this, googleConf)
                googleClient.signOut()

                startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN )


            }
        }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)

                if(account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }

            }catch (e: ApiException) {
                showAlert()
            }

        }
    }
}