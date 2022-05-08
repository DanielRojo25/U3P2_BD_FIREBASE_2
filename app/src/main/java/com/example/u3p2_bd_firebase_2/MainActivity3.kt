package com.example.u3p2_bd_firebase_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.u3p2_bd_firebase_2.databinding.ActivityMain3Binding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3 : AppCompatActivity() {
    var idSelecccionado = ""
    lateinit var binding: ActivityMain3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        idSelecccionado = intent.extras!!.getString("idseleccionado")!!

        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("Arrendamiento")
            .document(idSelecccionado)
            .get()
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("nombre"))
                binding.domicilio.setText(it.getString("domicilio"))
                binding.licencia.setText(it.getString("licencia"))
                binding.idauto.setText(it.getLong("idauto").toString())
                binding.fecha.setText(it.getString("fecha"))
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }

        binding.regresar.setOnClickListener {
            finish()
        }

        binding.actualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("Arrendamiento")
                .document(idSelecccionado)
                .update("nombre", binding.nombre.text.toString(),
                    "domicilio", binding.domicilio.text.toString(),
                    "licencia", binding.licencia.text.toString(),
                    "idauto", binding.idauto.text.toString().toInt(),
                    "fecha", binding.fecha.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this,"SE ACTULIZO EL ARRENDAMIENTO CON EXITO!", Toast.LENGTH_LONG)
                        .show()
                    binding.nombre.text.clear()
                    binding.domicilio.text.clear()
                    binding.licencia.text.clear()
                    binding.idauto.text.clear()
                    binding.fecha.text.clear()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }

    }
}