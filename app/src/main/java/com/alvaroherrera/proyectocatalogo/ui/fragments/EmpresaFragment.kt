import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alvaroherrera.proyectocatalogo.R
import com.alvaroherrera.proyectocatalogo.databinding.ListItemEmpresaBinding
import com.alvaroherrera.proyectocatalogo.ui.fragments.AgregarEmpresaDialogFragment
import com.alvaroherrera.proyectocatalogo.ui.home.Empresa
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EmpresaFragment : Fragment() {

    private lateinit var empresasAdapter: EmpresaAdapter
    private var empresasListenerRegistration: ListenerRegistration? = null
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_empresa, container, false)

        // Configurar RecyclerView y adaptador
        val empresasRecyclerView = view.findViewById<RecyclerView>(R.id.empresaRecyclerView)
        empresasAdapter = EmpresaAdapter(this)
        empresasRecyclerView.adapter = empresasAdapter

        // Obtener lista de empresas desde Firestore
        empresasListenerRegistration = db.collection("empresas")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Error al obtener empresas", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val empresas = snapshot.documents.mapNotNull { it.toObject(Empresa::class.java) }
                    empresasAdapter.submitList(empresas)
                }
            }

        // Configurar botón de agregar empresa
        val agregarEmpresaButton = view.findViewById<FloatingActionButton>(R.id.agregarEmpresaButton)
        agregarEmpresaButton.setOnClickListener {
            val dialog = AgregarEmpresaDialogFragment()
            dialog.show(childFragmentManager, "AgregarEmpresaDialogFragment")
        }

        return view
    }

    companion object {
        private const val TAG = "EmpresaFragment"
    }

    inner class EmpresaViewHolder(private val binding: ListItemEmpresaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(empresa: Empresa) {
            binding.nombreTextView.text = empresa.nombre
            binding.telefonoTextView.text = empresa.telefono
        }
    }

    inner class EmpresaAdapter(private val fragment: EmpresaFragment) : ListAdapter<Empresa, EmpresaViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
            val binding = ListItemEmpresaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return EmpresaViewHolder(binding)
        }

        override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
            val empresa = getItem(position)
            holder.bind(empresa)
        }
    }

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Empresa>() {
        override fun areItemsTheSame(oldItem: Empresa, newItem: Empresa): Boolean {
            return oldItem.nombre == newItem.nombre
        }

        override fun areContentsTheSame(oldItem: Empresa, newItem: Empresa): Boolean {
            return oldItem == newItem
        }
    }

    private fun agregarEmpresa(nombre: String, telefono: String) {
        val empresa = Empresa(nombre, telefono)
        db.collection("empresas")
            .add(empresa)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Empresa agregada con ID: ${documentReference.id}")
                empresasAdapter.notifyDataSetChanged() // Actualizar lista de empresas
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar empresa", e)
            }
    }
}
