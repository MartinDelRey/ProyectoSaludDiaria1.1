package com.example.proyectosaluddiaria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ReporteMes(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "peso") val peso:String,
    @ColumnInfo(name = "tiempoejerc") val tiempoejerc:String,
    @ColumnInfo(name = "cintura") val cintura:String,
    @ColumnInfo(name = "horassueno") val horassueno:String
) : Serializable