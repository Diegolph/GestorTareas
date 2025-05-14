package com.example.gestortareas.ui.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareas.data.model.Tarea
import com.example.gestortareas.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TareaViewModel(private val repository: TareaRepository, private val usuario: String) : ViewModel() {

    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> get() = _tareas

    init {
        cargarTareas()
    }

    private fun cargarTareas() {
        viewModelScope.launch {
            repository.obtenerPorUsuario(usuario).collect { lista ->
                _tareas.value = lista
        }
    }}

    fun agregarTarea(texto: String, fecha: String) {
        viewModelScope.launch {
            repository.insertar(Tarea(texto = texto, fecha = fecha, usuarioNombre = usuario))
            cargarTareas()
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.eliminar(tarea)
            cargarTareas()
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.actualizar(tarea)
            cargarTareas()
        }
    }
}