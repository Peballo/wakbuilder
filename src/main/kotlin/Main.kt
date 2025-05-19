import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.rememberWindowState
import components.BuildWindow
import components.MainWindow
import org.jetbrains.exposed.sql.ResultRow

@Composable
fun LoginRegisterDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onLoginRegister: (String, String) -> Unit
) {
    val modalBackgroundColor = Color(124, 205, 208)

    if (showDialog) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Dialog (
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        ) {
            Box (
                modifier = Modifier.width(300.dp).background(modalBackgroundColor, shape = RoundedCornerShape(8.dp)).padding(16.dp)
            ) {
                Column (
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Entrar / Registro", fontSize = 22.sp)

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Button (
                        onClick = {
                            onLoginRegister(username, password)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Entrar / Registrarse")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun App() {
    val ar = AccountsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))

    var account by remember { mutableStateOf<ResultRow?>(null) }
    var route by remember { mutableStateOf("home") }
    var showDialog by remember { mutableStateOf(false) }

    MaterialTheme {
        Box (
            modifier = Modifier.background(color = Color(95, 158,160)).padding(30.dp, 10.dp, 30.dp,30.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column() {
                        Text("Wakbuilder", color = Color.White, fontSize = 32.sp)
                    }

                    Column() {
                        Button(
                            onClick = {
                                showDialog = true
                                println("Showing dialog")
                            }
                        ) {
                            Text("Account")
                        }
                    }
                }

                if (route == "home") {
                    MainWindow(account) {
                            newRoute -> route = newRoute
                    }
                } else if (route == "builder") {
                    BuildWindow(account) {
                            newRoute -> route = newRoute
                    }
                }

                LoginRegisterDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onLoginRegister = { username, password ->
                        println("Username $username, password $password")
                    }
                )
            }
        }
    }
}

fun main() = application {
    Window(
        state = rememberWindowState(width = 1600.dp, height = 960.dp),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}