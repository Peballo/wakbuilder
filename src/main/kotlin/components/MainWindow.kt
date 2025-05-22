package components

import CharacterClass
import Equipments
import FixedGridImageSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import characterAvatar
import checkLevelInput
import coil3.compose.AsyncImage
import generarCodigo
import getSpriteHolderByName
import itemSprite
import objects.Accounts
import org.jetbrains.exposed.sql.ResultRow
import repositories.AccountsRepository
import repositories.BuildsRepository
import repositories.EquipmentsRepository

@Composable
fun MainWindow(account: String, onRouteChange: (String, String, String, Int, CharacterClass) -> Unit) {
    val br = BuildsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val ar = AccountsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val er = EquipmentsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val items = er.getAllEquipments()

    val statPillColor: Color = Color(170, 196,230)
    val panelColor: Color = Color(202, 230, 255)

    val buildColor = Color(0, 0,0)
    val errorColor = Color(240, 0, 100)

    var selectedClass by remember {
        mutableStateOf(CharacterClass(characterAvatar("sacrier"), "sacrier"))
    }
    var buildsPool: List<ResultRow> by remember { mutableStateOf(br.getAllBuilds()) }
    var buildName by remember { mutableStateOf(account.ifEmpty { "" })}
    var buildLevel by remember { mutableStateOf(1) }
    var buildNameIsCorrect by remember { mutableStateOf(true) }

    Row (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                modifier = Modifier.height(50.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (account.isNotEmpty()) {
                    TextButton(
                        colors =  ButtonDefaults.textButtonColors(
                            contentColor = panelColor,
                        ),
                        onClick = {

                        }
                    ) {
                        Text("Tus Builds", fontSize = 18.sp)
                    }
                }

                TextButton(
                    colors =  ButtonDefaults.textButtonColors(
                        contentColor = panelColor,
                    ),
                    onClick = {

                    }
                ) {
                    Text("Builds de la Comunidad", fontSize = 18.sp)
                }
            }

            // BUILDS POOL
            LazyColumn (
                modifier = Modifier.fillMaxSize().background(statPillColor, shape = RoundedCornerShape(8.dp)).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Por defecto salen las builds de la comunidad (al no estar registrado/logueado) cuando hay una cuenta logueada entonces salen tus builds
                items (buildsPool, key = {it[Builds.id]}) {
                    Button (
                        modifier = Modifier.height(80.dp).fillMaxWidth(),
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = panelColor)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box (
                                modifier = Modifier.size(60.dp).background(color = Color.Transparent, shape = RoundedCornerShape(8.dp)),
                            ) {
                                AsyncImage(
                                    model = characterAvatar(it[Builds.character].lowercase()),
                                    contentDescription = "character avatar"
                                )
                            }

                            Text("${it[Builds.name]} Nivel ${it[Builds.level]}", fontSize = 18.sp)

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.helmet] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.helmet]}!![Equipments.sprite_id]) else getSpriteHolderByName("helmet"),
                                    contentDescription = "helmet",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.neck] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.neck]}!![Equipments.sprite_id]) else getSpriteHolderByName("neck"),
                                    contentDescription = "neck",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.chest] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.chest]}!![Equipments.sprite_id]) else getSpriteHolderByName("chest"),
                                    contentDescription = "chest",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.left_ring] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.left_ring]}!![Equipments.sprite_id]) else getSpriteHolderByName("left_ring"),
                                    contentDescription = "left_ring",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.right_ring] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.right_ring]}!![Equipments.sprite_id]) else getSpriteHolderByName("right_ring"),
                                    contentDescription = "right_ring",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.boots] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.boots]}!![Equipments.sprite_id]) else getSpriteHolderByName("boots"),
                                    contentDescription = "boots",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.cape] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.cape]}!![Equipments.sprite_id]) else getSpriteHolderByName("cape"),
                                    contentDescription = "cape",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.epaulettes] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.epaulettes]}!![Equipments.sprite_id]) else getSpriteHolderByName("epaulettes"),
                                    contentDescription = "epaulettes",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.belt] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.belt]}!![Equipments.sprite_id]) else getSpriteHolderByName("belt"),
                                    contentDescription = "belt",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.first_weapon] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.first_weapon]}!![Equipments.sprite_id]) else getSpriteHolderByName("first_weapon"),
                                    contentDescription = "first_weapon",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.second_weapon] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.second_weapon]}!![Equipments.sprite_id]) else getSpriteHolderByName("second_weapon"),
                                    contentDescription = "second_weapon",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.emblem] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.emblem]}!![Equipments.sprite_id]) else getSpriteHolderByName("emblem"),
                                    contentDescription = "emblem",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.pet] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.pet]}!![Equipments.sprite_id]) else getSpriteHolderByName("pet"),
                                    contentDescription = "pet",
                                    alignment = Alignment.Center,
                                )
                            }

                            Box (
                                modifier = Modifier.size(64.dp).padding(8.dp).background(statPillColor, shape = RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (it[Builds.mount] > 0) itemSprite(items.find{i -> i[Equipments.id] == it[Builds.mount]}!![Equipments.sprite_id]) else getSpriteHolderByName("mount"),
                                    contentDescription = "mount",
                                    alignment = Alignment.Center,
                                )
                            }

                        }

                    }
                }
            }
        }

        Column (
            modifier = Modifier.width(300.dp).fillMaxHeight().background(statPillColor, shape = RoundedCornerShape(8.dp)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FixedGridImageSelector(
                    selectedClass = selectedClass,
                    onClassSelected = { newClass ->
                        selectedClass = newClass
                    }
                )

                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField (
                        value = buildName,
                        label = { Text("Nombre de la build") },
                        onValueChange = { buildName = it; buildNameIsCorrect = true },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors =  TextFieldDefaults.textFieldColors(
                            unfocusedLabelColor = if (buildNameIsCorrect) buildColor else errorColor
                        )
                    )
                    TextField (
                        value = "$buildLevel",
                        label = { Text("Nivel") },
                        onValueChange = { buildLevel = checkLevelInput(it) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedLabelColor = buildColor
                        )
                    )
                }
            }

            Row (

            ) {
                Button (
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = panelColor),
                    onClick = {
                        var cod = generarCodigo(br)
                        if (buildName.isNotEmpty()) {
                            var userId = -1

                            if (account.isNotEmpty()) {
                                userId = ar.getAccountByName(account)!![Accounts.id].value
                            }

                            br.insertBuild(cod, buildName, buildLevel, selectedClass.className, userId )
                            onRouteChange("builder", cod, buildName, buildLevel, selectedClass)
                        } else {
                            buildNameIsCorrect = false
                        }
                    }
                ) {
                    Text("Crear Nueva Build", fontSize = 24.sp)
                }
            }
        }
    }
}