package com.example.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.gestortareas.ui.inicio.InicioScreen
import com.example.gestortareas.ui.tareas.TareasScreen
import com.example.gestortareas.ui.theme.GestorTareasTheme
import com.example.gestortareas.data.preferences.PreferenciasUsuario


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GestorTareasTheme {
                val navController = rememberNavController()
                val preferencias = remember { PreferenciasUsuario(this) }
                val usuarioActual = preferencias.obtenerUsuario()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "inicio") {
                        composable("inicio") {
                            InicioScreen(
                                onLoginExitoso = {
                                    navController.navigate("tareas")
                                }
                            )
                        }
                        composable("tareas") {
                            if (usuarioActual != null) {
                                TareasScreen(navController, usuarioActual)
                            } else {
                                navController.navigate("inicio") {
                                    popUpTo("inicio") { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
