package com.example.gestortareas.data.dao

import androidx.room.*
import com.example.gestortareas.data.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Insert
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query("SELECT * FROM tareas WHERE usuarioNombre = :usuario")
    fun obtenerPorUsuario(usuario: String): Flow<List<Tarea>>
}
