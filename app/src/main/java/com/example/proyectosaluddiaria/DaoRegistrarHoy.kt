package com.example.proyectosaluddiaria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectosaluddiaria.ReporteMes

@Dao
interface DaoRegistrarHoy{
@Query("SELECT * FROM reportemes")
fun getAll():List<ReporteMes>
@Query("SELECT * FROM reportemes where peso like :pesoArgs LIMIT 1")
fun findByName(pesoArgs:String): ReporteMes
@Insert
fun agregar(vararg reporteMes: ReporteMes)
}