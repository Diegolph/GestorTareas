package com.example.gestortareas.data.preferences

import android.content.Context

class PreferenciasUsuario(context: Context) {

    private val prefs = context.getSharedPreferences("chato_prefs", Context.MODE_PRIVATE)

    fun guardarUsuario(nombre: String) {
        prefs.edit().putString("usuario_actual", nombre).apply()
    }

    fun obtenerUsuario(): String? {
        return prefs.getString("usuario_actual", null)
    }

    fun borrarUsuario() {
        prefs.edit().remove("usuario_actual").apply()
    }
}
