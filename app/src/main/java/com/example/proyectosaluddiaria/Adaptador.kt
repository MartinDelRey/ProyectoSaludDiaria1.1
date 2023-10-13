package com.example.proyectosaluddiaria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class Adaptador (private var contexto: Context, private val applicationContext: AppCompatActivity) :
    RecyclerView.Adapter<Adaptador.ViewHolderRegistro>(){

    //llena la lista
    val db = Room.databaseBuilder( applicationContext, AppDataBase::class.java, "registro").allowMainThreadQueries().build()
    val registrarHoy:List<ReporteMes> = db.registrarHoyDao().getAll()


    class ViewHolderRegistro(item: View):
        RecyclerView.ViewHolder(item) {
        var txtId: TextView = item.findViewById(R.id.DTID)
        var txtPeso: TextView = item.findViewById(R.id.DTPeso)
        var txtCintura: TextView = item.findViewById(R.id.DTCintura)
        var txtTiempoejer: TextView = item.findViewById(R.id.DTTiempEJE)
        var txtHorassueno: TextView = item.findViewById(R.id.DTHorasSueno)

    }



    // surve para especificar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRegistro {

        val layoutItem = LayoutInflater.from(parent.context).inflate(R.layout.datostabla,parent,false)
        return ViewHolderRegistro(layoutItem)
    }
    // sirve para decir cuantas vececes se va a repetir
    override fun getItemCount(): Int = registrarHoy.size


    // llena los datos en cada repeticion
    override fun onBindViewHolder(holder: ViewHolderRegistro, position: Int) {
        val registro= registrarHoy[position]
        holder.txtPeso.text = registro.peso
        holder.txtId.text = registro.id.toString()
        holder.txtTiempoejer.text = registro.tiempoejerc
        holder.txtCintura.text = registro.cintura
        holder.txtHorassueno.text = registro.horassueno

    }
}