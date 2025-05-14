package com.example.gestortareas.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gestortareas.data.dao.TareaDao
import com.example.gestortareas.data.dao.UsuarioDao
import com.example.gestortareas.data.model.Usuario
import com.example.gestortareas.data.model.Tarea


@Database(entities = [Usuario::class, Tarea::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chato_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCIA = instance
                instance
            }
        }
    }
}