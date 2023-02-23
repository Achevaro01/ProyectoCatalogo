package com.alvaroherrera.proyectocatalogo.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.alvaroherrera.proyectocatalogo.R
import com.alvaroherrera.proyectocatalogo.ui.home.Empresa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.ktx.Firebase


class AgregarEmpresaDialogFragment(private val onSubmitClickListener: FirebaseInAppMessagingClickListener) : DialogFragment() {



    private lateinit var db: FirebaseFirestore
    private lateinit var nombreEditText: EditText
    private lateinit var telefonoEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        db = Firebase.firestore

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_agregar_empresa, null)

            nombreEditText = view.findViewById(R.id.nombreEditText)
            telefonoEditText = view.findViewById(R.id.telefonoEditText)

            builder.setView(view)
                .setPositiveButton(R.string.guardar) { _, _ ->
                    val nombre = nombreEditText.text.toString()
                    val telefono = telefonoEditText.text.toString()
                    agregarEmpresa(nombre, telefono)
                }
                .setNegativeButton(R.string.cancelar) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Actividad no puede ser nula")
    }

    private fun agregarEmpresa(nombre: String, telefono: String) {
        val empresa = Empresa(nombre, telefono)
        db.collection("empresas")
            .add(empresa)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Empresa agregada con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar empresa", e)
            }
    }

    companion object {
        private const val TAG = "AgregarEmpresaDialogFragment"
    }
}

