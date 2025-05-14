package com.example.gestortareas.data.repository

import com.example.gestortareas.data.dao.TareaDao
import com.example.gestortareas.data.model.Tarea

class TareaRepository(private val tareaDao: TareaDao) {
    suspend fun insertar(t: Tarea) = tareaDao.insertar(t)
    suspend fun actualizar(t: Tarea) = tareaDao.actualizar(t)
    suspend fun eliminar(t: Tarea) = tareaDao.eliminar(t)
    suspend fun obtenerPorUsuario(nombre: String) = tareaDao.obtenerPorUsuario(nombre)
}
