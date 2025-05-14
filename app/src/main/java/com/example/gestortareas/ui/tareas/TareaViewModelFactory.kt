package com.example.gestortareas.ui.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestortareas.data.repository.TareaRepository

class TareaViewModelFactory(
    private val repository: TareaRepository,
    private val usuario: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TareaViewModel(repository, usuario) as T
    }
}