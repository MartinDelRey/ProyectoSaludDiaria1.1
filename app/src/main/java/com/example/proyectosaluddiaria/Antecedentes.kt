package com.example.proyectosaluddiaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room

class Antecedentes : AppCompatActivity() {

    lateinit var txtAlergias : EditText
    lateinit var txtEnfermeCro : EditText
    lateinit var txtPeso : EditText
    lateinit var txtMeta : EditText
    lateinit var txtIDD : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antecedentes)

        val btncancelar: Button = findViewById(R.id.btnCancelar3)

        btncancelar.setOnClickListener {
            //log.w("boton," "A pantalla")
            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)

        }
    }
    fun pdf(v: View){


        val id = txtIDD.text.toString().toInt()
        val alergias = txtAlergias.text.toString()
        val enfermecro = txtEnfermeCro.text.toString()
        val peso = txtPeso.text.toString()
        val meta = txtMeta.text.toString()
        //Crear objeto registro
        val registro = ReporteMes(id,alergias,enfermecro,peso,meta)
        //voy a crear el puntero a la bd
        val db = Room.databaseBuilder(applicationContext,
            AppDataBase::class.java,"registro").allowMainThreadQueries().build()
        db.registrarHoyDao().agregar(registro)
        Toast.makeText(this, "se grabo", Toast.LENGTH_SHORT).show()
    }
}