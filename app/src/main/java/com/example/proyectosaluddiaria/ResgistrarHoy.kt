package com.example.proyectosaluddiaria

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room


class ResgistrarHoy : AppCompatActivity() {

    lateinit var txtPeso : EditText
    lateinit var txtTiempoEjerc : EditText
    lateinit var txtCintura : EditText
    lateinit var txtHorasSueño : EditText

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var tvID: TextView
    private var registroID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgistrar_hoy)

        txtPeso = findViewById(R.id.txtPeso)
        txtTiempoEjerc = findViewById(R.id.txtTiempoEjerc)
        txtCintura = findViewById(R.id.txtCintura)
        txtHorasSueño = findViewById(R.id.txtHorasSueño)

        setupEditText(txtPeso, 3)
        setupEditText(txtTiempoEjerc, 2)
        setupEditText(txtCintura, 3)
        setupEditText(txtHorasSueño, 2)

        tvID = findViewById(R.id.tvID)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        registroID = sharedPreferences.getInt("lastId", 0)
        registroID++
        tvID.text = registroID.toString()

        val btncancelar: Button = findViewById(R.id.btnCancelar2)
        btncancelar.setOnClickListener {
            //log.w("boton," "A pantalla")
            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)
        }

    }
    private fun setupEditText(editText: EditText, maxLength: Int) {
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.filters = arrayOf(
            InputFilter.LengthFilter(maxLength),
            InputFilter { source, _, _, _, _, _ ->
                if (source.toString().matches(Regex("\\d*"))) {
                    null
                } else {
                    showToast("Solo se permiten números")
                    ""
                }
            }
        )
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        val lastId = sharedPreferences.getInt("lastId", 0) + 1
        tvID.text = lastId.toString()
    }

    fun agregarDato(v: View) {
        val pesoText = txtPeso.text.toString().trim()
        val tiempoEjercText = txtTiempoEjerc.text.toString().trim()
        val cinturaText = txtCintura.text.toString().trim()
        val horasSuenoText = txtHorasSueño.text.toString().trim()

        // Verificar si algún campo está vacío
        if (pesoText.isEmpty() || tiempoEjercText.isEmpty() || cinturaText.isEmpty() || horasSuenoText.isEmpty()) {
            showToast("Completa todos los campos antes de guardar.")
            return
        }

        // Construir las variables con el formato deseado
        val peso = "$pesoText kg"
        val tiempoejerc = "$tiempoEjercText h"
        val cintura = "$cinturaText cm"
        val horassueno = "$horasSuenoText h"

        registroID++

        val id = tvID.text.toString().toInt()

        val registro = ReporteMes(id, peso, tiempoejerc, cintura, horassueno)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("registro", registro)
        startActivity(intent)

        val db = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "registro").allowMainThreadQueries().build()
        db.registrarHoyDao().agregar(registro)

        with(sharedPreferences.edit()) {
            putInt("lastId", id)
            apply()
            commit()
        }

        // Actualiza el TextView con el nuevo ID
        tvID.text = registroID.toString()

        Toast.makeText(this, "Se grabó y se abrió ReporteDelMes", Toast.LENGTH_SHORT).show()
    }


}










