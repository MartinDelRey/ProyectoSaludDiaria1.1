package com.example.proyectosaluddiaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.view.View
import android.widget.Toast
import androidx.room.Room


class ResgistrarHoy : AppCompatActivity() {

    lateinit var txtPeso : EditText
    lateinit var txtTiempoEjerc : EditText
    lateinit var txtCintura : EditText
    lateinit var txtHorasSueño : EditText
    lateinit var txtID : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgistrar_hoy)

        txtID = findViewById(R.id.txtID)
        txtPeso = findViewById(R.id.txtPeso)
        txtTiempoEjerc = findViewById(R.id.txtTiempoEjerc)
        txtCintura = findViewById(R.id.txtCintura)
        txtHorasSueño = findViewById(R.id.txtHorasSueño)

        val btncancelar: Button = findViewById(R.id.btnCancelar2)
        btncancelar.setOnClickListener {
            //log.w("boton," "A pantalla")
            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)
        }
    }
        fun agregarDato(v: View){


            val id = txtID.text.toString().toInt()
            val peso = txtPeso.text.toString()
            val tiempoejerc = txtTiempoEjerc.text.toString()
            val cintura = txtCintura.text.toString()
            val horassueno = txtHorasSueño.text.toString()
            //Crear objeto registro
            val registro = ReporteMes(id,peso,tiempoejerc,cintura,horassueno)
            //voy a crear el puntero a la bd
            val db = Room.databaseBuilder(applicationContext,
                AppDataBase::class.java,"registro").allowMainThreadQueries().build()
            db.registrarHoyDao().agregar(registro)
            Toast.makeText(this, "se grabo", Toast.LENGTH_SHORT).show()
        }

}










