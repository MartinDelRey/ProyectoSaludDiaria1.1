package com.example.proyectosaluddiaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnAnt: Button = findViewById(R.id.btnAntecedentes)
        val btnRegistrar: Button = findViewById(R.id.btnRegistrarHoy)
        val btnReporte: Button = findViewById(R.id.btnReportedelMes)

         btnAnt.setOnClickListener {
             val intent1 = Intent(this,Antecedentes::class.java)
             startActivity(intent1)
         }
        btnRegistrar.setOnClickListener {
            val intent2 = Intent(this,ResgistrarHoy::class.java)
            startActivity(intent2)
        }
        btnReporte.setOnClickListener {
            val intent3 = Intent(this,ReporteDelMes::class.java)
            startActivity(intent3)
        }



        }
    }
