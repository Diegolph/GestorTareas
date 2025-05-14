package com.example.gestortareas.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareas.data.model.Usuario
import com.example.gestortareas.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun registrarUsuario(nombre: String, contrasena: String, confirmar: String) {
        viewModelScope.launch {
            if (nombre.isBlank() || contrasena.isBlank() || confirmar.isBlank()) {
                _mensaje.value = "Por favor, completa todos los campos."
                return@launch
            }

            if (contrasena != confirmar) {
                _mensaje.value = "Las contraseñas no coinciden."
                return@launch
            }

            val existente = repository.buscarUsuarioPorNombre(nombre)
            if (existente != null) {
                _mensaje.value = "El usuario ya existe."
            } else {
                val nuevoUsuario = Usuario(nombre = nombre, contrasena = contrasena)
                repository.insertarUsuario(nuevoUsuario)
                _mensaje.value = "Usuario registrado con éxito."
            }
        }
    }

    fun login(nombre: String, contrasena: String) {
        viewModelScope.launch {
            if (nombre.isBlank() || contrasena.isBlank()) {
                _mensaje.value = "Llena todos los campos"
                return@launch
            }

            val usuario = repository.buscarUsuarioPorNombre(nombre)
            if (usuario == null) {
                _mensaje.value = "El usuario no existe"
            } else if (usuario.contrasena != contrasena) {
                _mensaje.value = "Contraseña incorrecta"
            } else {
                _mensaje.value = "¡Bienvenido, $nombre!"
                //
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}

