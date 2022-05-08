package com.example.u3p2_bd_firebase_2.ui.home

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.u3p2_bd_firebase_2.MainActivity2
import com.example.u3p2_bd_firebase_2.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val listaIDs = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance()
            .collection("Autos")
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
                    var cadena = "Marca: ${documento.getString("marca")}\n" +
                            "Modelo: ${documento.getString("modelo")}\n" +
                            "Kilometraje: ${documento.getLong("km")}"
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
                "marca" to binding.marca.text.toString(),
                "modelo" to binding.modelo.text.toString(),
                "km" to binding.km.text.toString().toInt()
            )

            baseRemota.collection("Autos")
                .add(datos)
                .addOnSuccessListener {
                    Toast.makeText(context,"VEHICULO REGISTRADO CON EXITO!", Toast.LENGTH_LONG)
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
        baseRemota.collection("Autos")
            .document(idSelecccionado)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context,"SE ELIMINO VEHICULO CORRECTAMENTE", Toast.LENGTH_LONG)
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
        var otraVentana = Intent(context, MainActivity2::class.java)

        otraVentana.putExtra("idseleccionado", idSelecccionado)
        startActivity(otraVentana)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}