package com.example.proyectosaluddiaria

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface DaoAntecedentes {
    @Query("SELECT * FROM antecedentes")
    fun getAll():List<Antecedentes>
    @Query("SELECT * FROM antecedentes where peso like :pesoArgs LIMIT 1")
    fun findByName(pesoArgs:String):ReporteMes
    @Insert
    fun agregar(vararg reporteMes: ReporteMes)
}
