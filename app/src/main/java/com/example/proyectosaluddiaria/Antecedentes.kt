package com.example.proyectosaluddiaria

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream



import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell


class Antecedentes : AppCompatActivity() {

    lateinit var txtIDD: EditText
    lateinit var txtAlergias: EditText
    lateinit var txtEnfermeCro: EditText
    lateinit var txtPesoAnt: EditText
    lateinit var txtMeta: EditText
    lateinit var btnCrearPDF: Button

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
            crearPDF()
        } else {
            Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antecedentes)
        txtIDD = findViewById(R.id.txtIDD)
        txtAlergias = findViewById(R.id.txtAlergias)
        txtEnfermeCro = findViewById(R.id.txtEnfermeCro)
        txtPesoAnt = findViewById(R.id.txtPesoAnt)
        txtMeta = findViewById(R.id.txtMeta)
        btnCrearPDF = findViewById(R.id.btnCrearPDF)

        btnCrearPDF.setOnClickListener { requestPermission() }
    }

    override fun onResume() {
        super.onResume()
        val btncancelar: Button = findViewById(R.id.btnCancelar4)
        btncancelar.setOnClickListener {
            //log.w("boton," "A pantalla")
            val intent1 = Intent(this,MainActivity::class.java)
            startActivity(intent1)
        }
    }

    private fun requestPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            crearPDF()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        val id = txtIDD.text.toString()
        val alergias = txtAlergias.text.toString()
        val enfermecro = txtEnfermeCro.text.toString()
        val peso = txtPesoAnt.text.toString()
        val meta = txtMeta.text.toString()

        try {
            val carpeta = "/Antecedentes"
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + carpeta

            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show()
            }
            val file = File(dir, "Antecedentes.pdf")
            val fileOutputStream = FileOutputStream(file)

            val documento = Document()
            PdfWriter.getInstance(documento, fileOutputStream)

            documento.open()

            val titleFont = Font(Font.FontFamily.HELVETICA, 22f, Font.BOLD, BaseColor.PINK)
            val title = Paragraph("Antecedentes", titleFont)
            title.alignment = Element.ALIGN_CENTER

            documento.add(title)

            val table = PdfPTable(5)
            table.widthPercentage = 100f
            table.spacingAfter = 10f

            addCell(table, "ID", id)
            addCell(table, "Alergias", alergias)
            addCell(table, "Enfermedades Cronicas", enfermecro)
            addCell(table, "Peso", peso)
            addCell(table, "Meta", meta)

            documento.add(table)
            documento.close()

            Toast.makeText(this, "PDF creado en ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun addCell(table: PdfPTable, title: String, content: String) {
        val titleFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.BLACK)
        val contentFont = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.BLACK)

        val titleCell = PdfPCell(Phrase(title, titleFont))
        val contentCell = PdfPCell(Phrase(content, contentFont))

        table.addCell(titleCell)
        table.addCell(contentCell)
    }
}


