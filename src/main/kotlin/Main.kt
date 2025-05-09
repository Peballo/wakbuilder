import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.rememberWindowState
import components.BuildWindow

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box (
            modifier = Modifier.background(color = Color(95, 158,160)).padding(30.dp, 10.dp, 30.dp,30.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Text("Wakbuilder", color = Color.White, fontSize = 32.sp)
                BuildWindow()
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