package com.example.gestortareas.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val texto: String,
    val fecha: String,
    val usuarioNombre: String
)
