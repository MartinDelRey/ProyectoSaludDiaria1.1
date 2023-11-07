package com.example.proyectosaluddiaria

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ReporteMes::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    ///todos los ado´s

    abstract fun registrarHoyDao(): DaoRegistrarHoy

}