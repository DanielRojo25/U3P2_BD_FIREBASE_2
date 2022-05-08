package com.example.u3p2_bd_firebase_2.ui.dashboard

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u3p2_bd_firebase_2.MainActivity3
import com.example.u3p2_bd_firebase_2.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    val listaIDs = ArrayList<String>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance()
            .collection("Arrendamiento")
            .addSnapshotListener { query, error ->
                if (error!=null){
                    context?.let {
                        AlertDialog.Builder(it)
                            .setMessage(error.message)
                            .show()
                    }
                    return@addSnapshotListener
                }

                var arreglo = ArrayList<String>()
                listaIDs.clear()
                for (documento in query!!){
                    var cadena = "Nombre: ${documento.getString("nombre")}\n" +
                            "Domicilio: ${documento.getString("domicilio")}\n" +
                            "Licencia: ${documento.getString("licencia")}\n" +
                            "Auto: ${documento.getLong("idauto")}\n" +
                            "Fecha: ${documento.getString("fecha")}"
                    arreglo.add(cadena)
                    listaIDs.add(documento.id)
                }

                binding.lista.adapter = context?.let {
                    ArrayAdapter<String>(
                        it,
                        R.layout.simple_list_item_1, arreglo
                    )
                }

                binding.lista.setOnItemClickListener { parent, view, posicion, id ->
                    val idSelecccionado = listaIDs.get(posicion)

                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle("ATENCION")
                            .setMessage("¿Qué desea hacer con ID: ${idSelecccionado}")
                            .setNeutralButton("ELIMINAR"){d,i->
                                eliminar(idSelecccionado)
                            }
                            .setPositiveButton("ACTUALIZAR"){d,i->
                                actualizar(idSelecccionado)
                            }
                            .setNegativeButton("ACEPTAR"){d,i->}
                            .show()
                    }
                }
            }

        binding.insertar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()

            val datos = hashMapOf(
                "nombre" to binding.nombre.text.toString(),
                "domicilio" to binding.domicilio.text.toString(),
                "licencia" to binding.licencia.text.toString(),
                "idauto" to binding.idauto.text.toString().toInt(),
                "fecha" to binding.fecha.text.toString()
            )

            baseRemota.collection("Arrendamiento")
                .add(datos)
                .addOnSuccessListener {
                    Toast.makeText(context,"ARRENDAMIENTO REGISTRADO CON EXITO!", Toast.LENGTH_LONG)
                        .show()
                }
                .addOnFailureListener{
                    context?.let { it1 ->
                        AlertDialog.Builder(it1)
                            .setMessage(it.message)
                            .show()
                    }
                }
        }

        return root
    }

    private fun eliminar(idSelecccionado: String) {
        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("Arrendamiento")
            .document(idSelecccionado)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context,"SE ELIMINO ARRENDAMIENTO CORRECTAMENTE", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener{
                context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage(it.message)
                        .show()
                }
            }
    }

    private fun actualizar(idSelecccionado: String) {
        var otraVentana = Intent(context, MainActivity3::class.java)

        otraVentana.putExtra("idseleccionado", idSelecccionado)
        startActivity(otraVentana)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}