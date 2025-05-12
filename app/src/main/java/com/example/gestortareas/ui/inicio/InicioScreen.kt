package com.example.gestortareas.ui.inicio

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestortareas.data.database.AppDatabase
import com.example.gestortareas.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.collectLatest
import androidx.fragment.app.FragmentActivity



@Composable
fun InicioScreen(onLoginExitoso: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Instanciar DB y ViewModel con Factory
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UsuarioRepository(db.usuarioDao()) }
    val viewModel: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(repository))

    // Estados de UI
    var modoRegistro by remember { mutableStateOf(false) }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    // Mensaje desde ViewModel
    LaunchedEffect(viewModel.mensaje) {
        viewModel.mensaje.collectLatest { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                viewModel.limpiarMensaje()

                if (mensaje.contains("¡Bienvenido", ignoreCase = true)) {
                    onLoginExitoso()
                }
                if (mensaje.contains("registrado con éxito", ignoreCase = true)) {
                    usuario = ""
                    contrasena = ""
                    confirmar = ""
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHATO",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { modoRegistro = false },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text("Ingresar")
            }

            Button(
                onClick = { modoRegistro = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (modoRegistro) {
            OutlinedTextField(
                value = confirmar,
                onValueChange = { confirmar = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (modoRegistro) {
                    viewModel.registrarUsuario(usuario, contrasena, confirmar)
                } else {
                    viewModel.login(usuario, contrasena)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (modoRegistro) "Guardar" else "Acceder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Biometría
        if (!modoRegistro) {
            Button(
                onClick = {
                    val biometricManager = BiometricManager.from(context)
                    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        == BiometricManager.BIOMETRIC_SUCCESS
                    ) {
                        val executor = ContextCompat.getMainExecutor(context)

                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Autenticación biométrica")
                            .setSubtitle("Usa tu huella para acceder")
                            .setNegativeButtonText("Cancelar")
                            .build()

                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            val biometricPrompt = BiometricPrompt(
                                activity,
                                executor,
                                object : BiometricPrompt.AuthenticationCallback() {
                                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                        viewModel.login(usuario, contrasena)
                                    }

                                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                        Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onAuthenticationFailed() {
                                        Toast.makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            Toast.makeText(context, "No se pudo obtener la actividad", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "La autenticación biométrica no está disponible", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar huella digital")
            }
        }

    }
}
