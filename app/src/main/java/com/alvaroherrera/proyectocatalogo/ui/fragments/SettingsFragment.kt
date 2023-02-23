package com.alvaroherrera.proyectocatalogo.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alvaroherrera.proyectocatalogo.AuthActivity
import com.alvaroherrera.proyectocatalogo.R
import com.alvaroherrera.proyectocatalogo.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = activity?.intent?.extras
        setup(bundle?.getString("email") ?: "", bundle?.getString("provider") ?: "")
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSettingsBinding.inflate(layoutInflater)

    }

    private fun setup(email: String, provider: String) {
        //title = "Inicio"
        binding.emailTextView.text = email
        binding.providerTextView.text = provider

        binding.logOutButton.setOnClickListener {

            //Borrado de datos
            val prefs = activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)?.edit()
            prefs?.clear()
            prefs?.apply()



            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
        }

    }

}




