package com.example.gestortareas.data.repository

import com.example.gestortareas.data.dao.UsuarioDao
import com.example.gestortareas.data.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun insertarUsuario(usuario: Usuario) {
        usuarioDao.insertar(usuario)
    }

    suspend fun buscarUsuarioPorNombre(nombre: String): Usuario? {
        return usuarioDao.buscarPorNombre(nombre)
    }
}
