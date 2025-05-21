import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.*
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
import objects.Accounts
import org.jetbrains.exposed.sql.ResultRow
import repositories.AccountsRepository

@Composable
fun LoginRegisterDialog(
    showDialog: Boolean,
    ar: AccountsRepository,
    onDismiss: () -> Unit,
    onLoginRegister: (ResultRow?) -> Unit
) {
    val modalBackgroundColor = Color(124, 205, 208)

    if (showDialog) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordIsCorrect by remember { mutableStateOf(true) }
        val passwordColor = Color(95, 158,160)
        val errorColor = Color(240, 0, 100)
        val panelColor: Color = Color(202, 230, 255)

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
                        onValueChange = { username = it; passwordIsCorrect = true },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = passwordColor,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordIsCorrect = true },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = if (passwordIsCorrect) passwordColor else errorColor,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Button (
                        colors = ButtonDefaults.buttonColors(backgroundColor = panelColor),
                        onClick = {
                            if (checkUsername(username, ar)) {
                                if (checkAccount(username, password, ar)) {
                                    onLoginRegister(ar.getAccountByName(username))
                                    onDismiss()
                                } else {
                                    passwordIsCorrect = false
                                }
                            } else {
                                onLoginRegister(ar.insertAccount(username, sha256(password)))
                                onDismiss()
                            }
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
    var username by remember { mutableStateOf("") }
    var account by remember { mutableStateOf<ResultRow?>(null) }
    var route by remember { mutableStateOf("home") }
    var newBuildCode by remember { mutableStateOf("") }
    var newBuildName by remember { mutableStateOf("") }
    var newBuildLevel by remember { mutableStateOf(1) }
    var newSelectedClass by remember { mutableStateOf(CharacterClass("","")) }
    var showDialog by remember { mutableStateOf(false) }
    val panelColor: Color = Color(202, 230, 255)

    MaterialTheme {
        Box (
            modifier = Modifier.background(color = Color(95, 158,160)).padding(30.dp, 10.dp, 30.dp,30.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column() {
                        Text("Wakbuilder", color = Color.White, fontSize = 32.sp)
                    }

                    Column() {
                        if (route == "home") {
                            if (account != null) {
                                Text(account!![Accounts.username])
                            } else {
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = panelColor),
                                    onClick = {
                                        showDialog = true
                                    }
                                ) {
                                    Text("Account")
                                }
                            }
                        } else if (route == "builder") {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (account != null) {
                                    Text(account!![Accounts.username])
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = panelColor),
                                    onClick = {
                                        route = "home"
                                    }
                                ) {
                                    Text("Volver")
                                }
                            }
                        }
                    }
                }

                if (route == "home") {

                    if (account != null) username = account!![Accounts.username]
                    MainWindow(username) {
                            newRoute, buildCode, buildName, buildLevel, selectedClass -> route = newRoute; newBuildCode = buildCode; newBuildName = buildName; newBuildLevel = buildLevel; newSelectedClass = selectedClass
                    }
                } else if (route == "builder") {
                    BuildWindow(account, newBuildCode, newBuildName, newBuildLevel, newSelectedClass) {
                            newRoute -> route = newRoute
                    }
                }

                LoginRegisterDialog(
                    showDialog = showDialog,
                    ar = ar,
                    onDismiss = { showDialog = false },
                    onLoginRegister = { user ->
                        account = user
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