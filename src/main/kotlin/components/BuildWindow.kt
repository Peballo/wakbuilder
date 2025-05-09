package components

import EffectsRepository
import EquipmentsRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.exposed.sql.ResultRow

fun checkLevelInput(levelLabel: String): Int {
    // Check if levelLabel has any symbol that isn't a number
    val level: Int = levelLabel.toIntOrNull() ?: 0

    return if (level >= 1 && level <= 245) level
    else {
        if (level <= 0) 1
        else 245
    }
}

@Composable
fun EquipmentCell(item: ResultRow, color: Color) {
    Column (
        modifier = Modifier.background(Color(150, 190,250), RoundedCornerShape(8.dp)).padding(8.dp)
    ) {
        // Name of the item
        Text(item[Equipments.name_es], fontWeight = FontWeight.SemiBold)

        // Item sprite, level, type and rarity
        Row (

        ) {
            Box (
                modifier = Modifier.padding(8.dp).background(color, RoundedCornerShape(8.dp))
            ) {
                AsyncImage (model = "https://vertylo.github.io/wakassets/items/${item[Equipments.sprite_id]}.png", contentDescription = "item sprite")
            }

        }
    }

}

@Composable
fun BuildWindow() {
    // Repositories
    val er: EquipmentsRepository = EquipmentsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val efr: EffectsRepository = EffectsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")

    // Colors
    val statPillColor: Color = Color(170, 196,230) // The color for the boxes where the HP, AP, MP and WP are
    val panelColor: Color = Color(202, 230, 255) // The color for each big box where data is shown

    // Stats
    var level by remember { mutableStateOf(200) }
    var actionPoints by remember { mutableStateOf(6) }
    var movementPoints by remember { mutableStateOf(3) }
    var wakfuPoints by remember { mutableStateOf(6) }

    // Item pool
    var itemPool: List<ResultRow> by remember { mutableStateOf(emptyList()) }

    Row (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Character Box (LEFT)
        Box (
            modifier = Modifier.background(color = panelColor, shape = RoundedCornerShape(10.dp))
        ) {
            Column (
                modifier = Modifier.width(300.dp).fillMaxHeight()
            ) {
                // Character image, name and level
                Row (
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AsyncImage(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/classes/sacrier.png",
                        contentDescription = "character image",
                    )

                    Column {
                        Text("Visama", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        TextField(
                            value = "$level",
                            onValueChange = { level = checkLevelInput( it) },
                            label = { Text("Nivel") },
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = statPillColor)
                        )
                    }
                }

                // Health points, AP, MP and WP/QP
                Row (
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column (
                        modifier = Modifier.width(60.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/empty_coin.png",
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/health_points.png",
                                contentDescription = "hp"
                            )
                        }

                        Text("${level*10+50}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.width(60.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/empty_coin.png",
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/action_points.png",
                                contentDescription = "ap"
                            )
                        }

                        Text("$actionPoints", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.width(60.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/empty_coin.png",
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/movement_points.png",
                                contentDescription = "mp"
                            )
                        }

                        Text("$movementPoints", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.width(60.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/empty_coin.png",
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/wakfu_points.png",
                                contentDescription = "wp"
                            )
                        }

                        Text("$wakfuPoints", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // RIGHT SIDE
        Column (
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // BUILD BOX
            Row(
                modifier = Modifier.fillMaxWidth().background(color = panelColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Elements order box
                Column(

                ) {

                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(134, level)
                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/HEAD.png",
                        contentDescription = "helmet",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/NECK.png",
                        contentDescription = "necklace",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/CHEST.png",
                        contentDescription = "breastplate",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEFT_HAND.png",
                        contentDescription = "left ring",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/RIGHT_HAND.png",
                        contentDescription = "right ring",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEGS.png",
                        contentDescription = "boots",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BACK.png",
                        contentDescription = "cape",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SHOULDERS.png",
                        contentDescription = "epaulettes",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BELT.png",
                        contentDescription = "belt",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/FIRST_WEAPON.png",
                        contentDescription = "primary weapon",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SECOND_WEAPON.png",
                        contentDescription = "secondary weapon",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/ACCESSORY.png",
                        contentDescription = "emblem",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/PET.png",
                        contentDescription = "mount",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {

                    }
                ) {
                    AsyncImage(
                        model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/PET.png",
                        contentDescription = "pet",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
            }

            // ITEM POOL AND FILTERS BOXES
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyVerticalGrid (
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxHeight().weight(1f).background(color = panelColor, shape = RoundedCornerShape(10.dp)).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(itemPool) {
                            item -> EquipmentCell(item, statPillColor)
                    }
                }
                Box (
                    modifier = Modifier.fillMaxHeight().width(220.dp).background(color = panelColor, shape = RoundedCornerShape(10.dp)).padding(10.dp)
                ) {
                    Text("Filters")
                }
            }

        }
    }
}