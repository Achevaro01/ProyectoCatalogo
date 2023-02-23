package com.alvaroherrera.proyectocatalogo.ui.adapter

import EmpresaFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alvaroherrera.proyectocatalogo.databinding.ListItemEmpresaBinding
import com.alvaroherrera.proyectocatalogo.ui.home.Empresa
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EmpresaAdapter(private val fragment: EmpresaFragment) :
    ListAdapter<Empresa, EmpresaAdapter.EmpresaViewHolder>(DIFF_CALLBACK) {

    private val db = Firebase.firestore
    private var empresasListenerRegistration: ListenerRegistration? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val binding = ListItemEmpresaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmpresaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = getItem(position)
        holder.bind(empresa)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Obtener lista de empresas desde Firestore
        empresasListenerRegistration = db.collection("empresas")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Error al obtener empresas", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val empresas = snapshot.documents.mapNotNull { it.toObject(Empresa::class.java) }
                    submitList(empresas)
                }
            }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        empresasListenerRegistration?.remove()
    }

    inner class EmpresaViewHolder(private val binding: ListItemEmpresaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(empresa: Empresa) {
            binding.nombreTextView.text = empresa.nombre
            binding.telefonoTextView.text = empresa.telefono
        }
    }

    companion object {
        private const val TAG = "EmpresaAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Empresa>() {
            override fun areItemsTheSame(oldItem: Empresa, newItem: Empresa): Boolean {
                return oldItem.nombre == newItem.nombre
            }

            override fun areContentsTheSame(oldItem: Empresa, newItem: Empresa): Boolean {
                return oldItem == newItem
            }
        }
    }
}



