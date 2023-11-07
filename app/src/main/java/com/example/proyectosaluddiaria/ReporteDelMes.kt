package com.example.proyectosaluddiaria


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.pm.PackageManager
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectosaluddiaria.databinding.ActivityReporteDelMesBinding
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import android.Manifest
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ReporteDelMes : AppCompatActivity() {


    lateinit var binding: ActivityReporteDelMesBinding

    var listaReporteDelMes: ArrayList<ReporteMes> = arrayListOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAceptado ->
        if (isAceptado) Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }
    lateinit var rvReporte: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteDelMesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCrearPDF.setOnClickListener {
            verificarPermisos(it)
        }
        rvReporte = findViewById(R.id.rvReporte)
        ActivityResultContracts.RequestPermission()
    }

        override fun onResume() {
            super.onResume()
            val adaptador = Adaptador(this,this)
            rvReporte.adapter = adaptador
            rvReporte.layoutManager = LinearLayoutManager(this)


        val btncancelar: Button = findViewById(R.id.btnCancelar4)
        btncancelar.setOnClickListener {
            //log.w("boton," "A pantalla")

            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestPermissions(permission, 101)

            }

    }
    private fun crearPDF() {

        try {
            val carpeta = "/archivospdf"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

            val dirDownload = File(path)
            if (!dirDownload.exists()) {
                dirDownload.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }

            val dir = File(path + carpeta)
            if (!dir.exists()) {
                dir.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }
            val file = File(dir, "Reporte.pdf")
            val fileOutputStream = FileOutputStream(file)

            val documento = Document()
            PdfWriter.getInstance(documento, fileOutputStream)

            documento.open()

            val titulo = Paragraph(
                "Reporte Diario \n\n\n",
                FontFactory.getFont("arial", 22f, Font.BOLD, BaseColor.BLUE)
            )

            documento.add(titulo)

            // Crear celdas y agregar contenido a cada una
            val tabla = PdfPTable(5)
            val celdaDia = PdfPCell(Phrase("DIA"))
            val celdaPeso = PdfPCell(Phrase("PESO"))
            val celdaTiempoEjercicio = PdfPCell(Phrase("TIEMPO DE EJERCICIO"))
            val celdaCintura = PdfPCell(Phrase("CINTURA"))
            val celdaHorasSueno = PdfPCell(Phrase("HORAS DE SUEÑO"))

            tabla.addCell(celdaDia)
            tabla.addCell(celdaPeso)
            tabla.addCell(celdaTiempoEjercicio)
            tabla.addCell(celdaCintura)
            tabla.addCell(celdaHorasSueno)

            for (i in listaReporteDelMes.indices) {
                val reporte = listaReporteDelMes[i]

                val celdaDiaValor = PdfPCell(Phrase(reporte.id.toString()))
                val celdaPesoValor = PdfPCell(Phrase(reporte.peso))
                val celdaTiempoEjercicioValor = PdfPCell(Phrase(reporte.tiempoejerc))
                val celdaCinturaValor = PdfPCell(Phrase(reporte.cintura))
                val celdaHorasSuenoValor = PdfPCell(Phrase(reporte.horassueno))

                tabla.addCell(celdaDiaValor)
                tabla.addCell(celdaPesoValor)
                tabla.addCell(celdaTiempoEjercicioValor)
                tabla.addCell(celdaCinturaValor)
                tabla.addCell(celdaHorasSuenoValor)
            }


            documento.add(tabla)
            documento.close()
            Toast.makeText(this, "PDF creado en ${file.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun verificarPermisos(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
                crearPDF()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Snackbar.make(
                    view,
                    "ESTE PERMISO ES NECESARIO PARA CREAR EL ARCHIVO",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    }
}
