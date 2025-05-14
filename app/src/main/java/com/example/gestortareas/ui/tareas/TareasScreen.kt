package com.example.gestortareas.ui.tareas

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavHostController
import com.example.gestortareas.data.model.Tarea
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestortareas.data.database.AppDatabase
import com.example.gestortareas.data.repository.TareaRepository
import com.example.gestortareas.data.dao.TareaDao



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(navController: NavHostController, usuarioActual: String) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val tareaRepository = remember { TareaRepository(db.tareaDao()) }
    val viewModel: TareaViewModel = viewModel(
        factory = TareaViewModelFactory(tareaRepository, usuarioActual)
    )
    val tareas by viewModel.tareas.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pendientes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Pendientes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(tareas) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onEditar = { nuevaTarea ->
                            viewModel.actualizarTarea(nuevaTarea)
                        },
                        onEliminar = {
                            viewModel.eliminarTarea(tarea)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showDialog) {
            NuevaTareaDialog(
                usuario = usuarioActual,
                onGuardar = { nueva ->
                    viewModel.agregarTarea(nueva.texto, nueva.fecha)
                    showDialog = false
                },
                onCancelar = { showDialog = false }
            )
        }

    }
}



@Composable
fun TareaItem(
    tarea: Tarea,
    onEditar: (Tarea) -> Unit,
    onEliminar: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tarea.texto, fontWeight = FontWeight.Medium)
                Text("Fecha límite: ${tarea.fecha}", fontSize = 12.sp)
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Editar texto") }, onClick = {
                        val nuevo = tarea.copy(texto = tarea.texto + " (editado)")
                        onEditar(nuevo)
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Cambiar fecha") }, onClick = {
                        // Para efectos visuales rápidos:
                        val nuevo = tarea.copy(fecha = "01/01/26")
                        onEditar(nuevo)
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Eliminar") }, onClick = {
                        onEliminar()
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun NuevaTareaDialog(
    usuario: String,
    onGuardar: (Tarea) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva tarea") },
        text = {
            Column {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .  clickable {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val formato = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                                cal.set(y, m, d)
                                fecha = formato.format(cal.time)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = {},
                        label = { Text("Fecha límite") },
                        readOnly = true,
                        enabled = false, // Evita que se active el teclado
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (texto.isNotBlank() && fecha.isNotBlank()) {
                    onGuardar(Tarea(texto = texto, fecha = fecha, usuarioNombre = usuario))
                } else {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
