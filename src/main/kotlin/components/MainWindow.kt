package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.exposed.sql.ResultRow

@Composable
fun MainWindow(account: ResultRow?, onRouteChange: (String) -> Unit) {

    Row (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column (

        ) {
            Row (
                modifier = Modifier.height(50.dp)
            ) {
                if (account != null) {
                    TextButton(
                        onClick = {

                        }
                    ) {
                        Text("Tus Builds")
                    }
                }

                TextButton(
                    onClick = {

                    }
                ) {
                    Text("Builds de la Comunidad")
                }


            }
        }

        Column (

        ) {
            Row (
                modifier = Modifier.height(50.dp)
            ) {
                Button (
                    onClick = {
                        // TODO: SEND DEFAULT PARAMS TO BUILD
                        // TODO: CREATE RANDOM ID
                        onRouteChange("builder")
                    }
                ) {
                    Text("Crear Nueva Build", fontSize = 24.sp)
                }
            }
        }
    }
}