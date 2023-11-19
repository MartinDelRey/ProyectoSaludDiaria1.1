package com.example.proyectosaluddiaria

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
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

import android.text.InputFilter
import android.text.InputType
import android.text.Spanned


import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects


class Antecedentes : AppCompatActivity() {

    lateinit var txtIDD: EditText
    lateinit var txtAlergias: EditText
    lateinit var txtEnfermeCro: EditText
    lateinit var txtPesoAnt: EditText
    lateinit var txtMeta: EditText
    lateinit var btnCrearPDF: Button
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var baseDocumentTreeUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAceptado ->
        if (isAceptado) Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
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

        txtPesoAnt.inputType = InputType.TYPE_CLASS_NUMBER
        txtPesoAnt.filters = arrayOf(InputFilter.LengthFilter(3), InputFilter { source, _, _, _, _, _ ->
            if (source.toString().matches(Regex("\\d*"))) {
                null
            } else {
                showToast("Solo se permiten números")
                ""
            }
        })


        btnCrearPDF.setOnClickListener {
            // Realizar la validación antes de crear el PDF
            if (validarCampos()) {
                verificarPermisos()
            }
        }

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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun validarCampos(): Boolean {
        val id = txtIDD.text.toString()
        val alergias = txtAlergias.text.toString()
        val enfermecro = txtEnfermeCro.text.toString()
        val peso = txtPesoAnt.text.toString()
        val meta = txtMeta.text.toString()

        if (id.isEmpty() || alergias.isEmpty() || enfermecro.isEmpty() || peso.isEmpty() || meta.isEmpty())  {
            showToast("Completa todos los campos antes de guardar.")
            return false
        }

        return true
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
        val peso = "${txtPesoAnt.text} kg"
        val meta = txtMeta.text.toString()


        try {

            val directory = DocumentFile.fromTreeUri(this,baseDocumentTreeUri!!)
            val file = directory!!.createFile("application/pdf", "Antecedentes.pdf")
            val pdf: ParcelFileDescriptor = this.getContentResolver().openFileDescriptor(file!!.uri, "w")!!
            val fileOutputStream = FileOutputStream(pdf.fileDescriptor)

            val documento = Document()
            PdfWriter.getInstance(documento, fileOutputStream)

            documento.open()

            val titleFont = Font(Font.FontFamily.HELVETICA, 22f, Font.BOLD, BaseColor.PINK)
            val title = Paragraph("Antecedentes \n\n\n", titleFont)
            title.alignment = Element.ALIGN_CENTER

            documento.add(title)

            // Agregar la fecha actual
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaActual = dateFormat.format(Date())

            val fechaFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.BLACK)
            val fecha = Paragraph("Fecha: $fechaActual", fechaFont)
            fecha.alignment = Element.ALIGN_RIGHT
            fecha.indentationRight = 20f
            fecha.spacingAfter = 10f

            documento.add(fecha)

            val table = PdfPTable(2)
            table.widthPercentage = 100f
            table.spacingAfter = 10f

            addCell(table, "ID", id)
            addCell(table, "Alergias", alergias)
            addCell(table, "Enfermedades Cronicas", enfermecro)
            addCell(table, "Peso", peso)
            addCell(table, "Meta", meta)

            documento.add(table)
            documento.close()
            val uri:Uri = file.uri
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            //share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(share)



        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        }
    }

    private fun verificarPermisos() {
        launchBaseDirectoryPicker()
    }
    fun launchBaseDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher!!.launch(intent)
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




