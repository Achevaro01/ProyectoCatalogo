package com.alvaroherrera.proyectocatalogo

import EmpresaFragment
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alvaroherrera.proyectocatalogo.databinding.ActivityHomeBinding
import com.alvaroherrera.proyectocatalogo.ui.fragments.SettingsFragment


enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    companion object {
        lateinit var context: Context
    }

    private lateinit var binding: ActivityHomeBinding
    private var contador = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")


        // GUARDADO DE DATOS

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        val empresaFragment = EmpresaFragment()
        val settingsFragment = SettingsFragment()

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.empresaFragment -> makeCurrentFragment(empresaFragment)
                R.id.settingsFragment -> makeCurrentFragment(settingsFragment)
            }
            true
        }




    }

    private fun makeCurrentFragment(fragment : Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.frame_container, fragment)
        commit()
    }

    override fun onBackPressed() {
        if(binding.bottomNavigation.selectedItemId == R.id.empresaFragment){

            if (contador == 0) {
                finish()
            } else {
                super.onBackPressed()
            }
        } else {
            binding.bottomNavigation.selectedItemId = R.id.empresaFragment
        }
    }


}