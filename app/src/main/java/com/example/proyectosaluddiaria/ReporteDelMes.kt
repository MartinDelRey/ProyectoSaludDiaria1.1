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
import android.app.Activity
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Objects

class ReporteDelMes : AppCompatActivity() {


    lateinit var binding: ActivityReporteDelMesBinding

    //var listaReporteDelMes: ArrayList<ReporteMes> = arrayListOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAceptado ->
        if (isAceptado) Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }
    lateinit var rvReporte: RecyclerView
    private lateinit var adaptador: Adaptador
    private var listaReporteDelMes: List<ReporteMes> = emptyList()
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var baseDocumentTreeUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteDelMesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCrearPDF.setOnClickListener {
            verificarPermisos(it)
        }
        rvReporte = findViewById(R.id.rvReporte)

        adaptador = Adaptador(this, this)
        rvReporte.adapter = adaptador
        rvReporte.layoutManager = LinearLayoutManager(this)

        // Asigna la lista de reportes al adaptador
        listaReporteDelMes = adaptador.registrarHoy

        launcher = registerForActivityResult( ActivityResultContracts.StartActivityForResult()){
            result->
            run{
                if (result.resultCode == Activity.RESULT_OK){
                    baseDocumentTreeUri = Objects.requireNonNull(result.data)!!.data
                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    result.data!!.data?.let{
                        this.getContentResolver().takePersistableUriPermission(it, takeFlags)

                    }
                }
            }
            crearPDF()
        }
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


            /*val carpeta = "/archivospdf"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + carpeta

            val dirr = File(path)
            if (!dirr.exists()) {
                dirr.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }


            val dir = File(path + carpeta)
            if (!dir.exists()) {
                dir.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }*/

            val directory = DocumentFile.fromTreeUri(this,baseDocumentTreeUri!!)
            val file = directory!!.createFile("application/pdf", "Reporte.pdf")
                    val pdf: ParcelFileDescriptor = this.getContentResolver().openFileDescriptor(file!!.uri, "w")!!
            val fileOutputStream = FileOutputStream(pdf.fileDescriptor)


            val documento = Document()
            PdfWriter.getInstance(documento, fileOutputStream)

            documento.open()

            val titulo = Paragraph(
                "Reporte Diario \n\n\n",
                FontFactory.getFont("arial", 22f, Font.BOLD, BaseColor.BLUE)

            )
            titulo.alignment = Element.ALIGN_CENTER
            documento.add(titulo)
            val listaReportes = adaptador.registrarHoy

            // Verificar que el registro no sea nulo
            if (listaReportes.isNotEmpty()) {
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

                for (i in listaReportes.indices) {
                    val reporte = listaReportes[i]

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
            } else {
                Log.d("DatosReporte", "La lista de datos está vacía")
            }
            documento.close()

            val uri:Uri = file.uri
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            //share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(share)



        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun verificarPermisos(view: View) {
        launchBaseDirectoryPicker()
    }
    fun launchBaseDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher!!.launch(intent)
    }
}
