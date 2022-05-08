package com.example.u3p2_bd_firebase_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.u3p2_bd_firebase_2.databinding.ActivityMain2Binding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {
    var idSelecccionado = ""
    lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        idSelecccionado = intent.extras!!.getString("idseleccionado")!!

        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("Autos")
            .document(idSelecccionado)
            .get()
            .addOnSuccessListener {
                binding.marca.setText(it.getString("marca"))
                binding.modelo.setText(it.getString("modelo"))
                binding.km.setText(it.getLong("kilometraje").toString())
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
            baseRemota.collection("Autos")
                .document(idSelecccionado)
                .update("marca", binding.marca.text.toString(),
                    "modelo", binding.modelo.text.toString(),
                    "km", binding.km.text.toString().toInt())
                .addOnSuccessListener {
                    Toast.makeText(this,"SE ACTULIZO EL VEHICULO CON EXITO!", Toast.LENGTH_LONG)
                        .show()
                    binding.marca.text.clear()
                    binding.modelo.text.clear()
                    binding.km.text.clear()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }

    }
}