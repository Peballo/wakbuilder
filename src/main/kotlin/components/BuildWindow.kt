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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import parseEffect

fun checkLevelInput(levelLabel: String): Int {
    // Check if levelLabel has any symbol that isn't a number
    val level: Int = levelLabel.toIntOrNull() ?: 0

    return if (level >= 1 && level <= 245) level
    else {
        if (level <= 0) 1
        else 245
    }
}

fun rarityColors(rarity: Int): Color {
    return when (rarity) {
        1 -> Color(255,255,255) // Common
        2 -> Color(54,196,54) // Rare
        3 -> Color(221,127,19) // Mythic
        4 -> Color(255, 239, 100) // Legend
        5 -> Color(197, 112, 239) // Relic
        6 -> Color(34, 209, 205) // Souvenir
        7 -> Color(255, 152, 207) // Epic
        else -> Color(40,20,180) // Antique
    }

}


@Composable
fun EquipmentCell(item: ResultRow, color: Color) {
    val efr: EffectsRepository = EffectsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")

    val nameColor: Color = rarityColors(item[Equipments.rarity])

    var effects: List<ResultRow> = emptyList()

    var parsedEffects: List<String> = emptyList()

    for ( effect in item[Equipments.effects]) {
        var e = efr.getEffectById(effect)

        if (e != null) {
            effects = effects.plus(e)
        }
    }

    if (effects.isNotEmpty()) {
        effects.sortedBy {
            it[Effects.action]
        }

        for (ef in effects) {
            parsedEffects = parsedEffects.plus(parseEffect(ef, item[Equipments.level]))
        }
    }

    Button(
        modifier = Modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(Color(171, 214, 250)),
        shape = RoundedCornerShape(8.dp),
        onClick = {}
    ) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            // Name of the item
            Text(item[Equipments.name_es], fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = nameColor)

            // Item sprite, level, type and rarity
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Box (
                    modifier = Modifier.padding(8.dp).background(Color(150, 190,250), RoundedCornerShape(8.dp))
                ) {
                    AsyncImage (
                        model = "https://vertylo.github.io/wakassets/items/${item[Equipments.sprite_id]}.png",
                        contentDescription = "item sprite"
                    )
                }

                Column (
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Nivel ${item[Equipments.level]}", fontSize = 16.sp)

                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = "https://tmktahu.github.io/WakfuAssets/itemTypes/${item[Equipments.item_type]}.png",
                            contentDescription = "type"
                        )
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = "https://vertylo.github.io/wakassets/rarities/${item[Equipments.rarity]}.png",
                            contentDescription = "rarity"
                        )
                    }
                }
            }

            // Item chars
            Row {
                Column {
                    parsedEffects.forEach {
                        Text(it, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun BuildWindow() {
    // Repositories
    val er: EquipmentsRepository = EquipmentsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val efr: EffectsRepository = EffectsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val lazyGridState = rememberLazyGridState()

    // Colors
    val statPillColor: Color = Color(170, 196,230) // The color for the boxes where the HP, AP, MP and WP are
    val panelColor: Color = Color(202, 230, 255) // The color for each big box where data is shown

    // Stats
    var level by remember { mutableStateOf(200) }
    var actionPoints by remember { mutableStateOf(6) }
    var movementPoints by remember { mutableStateOf(3) }
    var wakfuPoints by remember { mutableStateOf(6) }
    var fireAttack by remember { mutableStateOf(9999) }
    var fireRessis by remember { mutableStateOf(9999) }
    var airAttack by remember { mutableStateOf(9999) }
    var airRessis by remember { mutableStateOf(9999) }
    var earthAttack by remember { mutableStateOf(9999) }
    var earthRessis by remember { mutableStateOf(9999) }
    var waterAttack by remember { mutableStateOf(9999) }
    var waterRessis by remember { mutableStateOf(9999) }
    var dominioMelee by remember { mutableStateOf(9999) }
    var dominioDistancia by remember { mutableStateOf(9999) }
    var dominioCritico by remember { mutableStateOf(9999) }
    var dominioEspalda by remember { mutableStateOf(9999) }
    var dominioBerserk by remember { mutableStateOf(9999) }
    var dominioCuras by remember { mutableStateOf(9999) }
    var armaduraDada by remember { mutableStateOf(9999) }
    var armaduraRecibida by remember { mutableStateOf(9999) }
    var resistenciaCritica by remember { mutableStateOf(9999) }
    var resitenciaEspalda by remember { mutableStateOf(9999) }
    var dañoInfligido by remember { mutableStateOf(15) }
    var probabilidadCritica by remember { mutableStateOf(50) }
    var curasRealizadas by remember { mutableStateOf(25) }
    var anticipacion by remember { mutableStateOf(50) }
    var iniciativa by remember { mutableStateOf(50) }
    var prospeccion by remember { mutableStateOf(50) }
    var sabiduria by remember { mutableStateOf(50) }
    var alcance by remember { mutableStateOf(2) }
    var placaje by remember { mutableStateOf(50) }
    var esquiva by remember { mutableStateOf(50) }
    var control by remember { mutableStateOf(50) }
    var voluntad by remember { mutableStateOf(50) }


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
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
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
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
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
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
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
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
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

                //Dominios y resistencias secundarias
                Row (
                    modifier = Modifier.padding(10.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Dominios
                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        //Fuego
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/fire_coin.png",
                                        contentDescription = "fire dmg"
                                    )
                                }

                            }
                            Text("$fireAttack", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Tierra
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/earth_coin.png",
                                        contentDescription = "earth dmg"
                                    )
                                }
                            }
                            Text("$earthAttack", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Aire
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/air_coin.png",
                                        contentDescription = "air dmg"
                                    )
                                }
                            }
                            Text("$airAttack", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Water
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/water_coin.png",
                                        contentDescription = "water dmg"
                                    )
                                }
                            }

                            Text("$waterAttack", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/block.png",
                                        contentDescription = "fire resi"
                                    )
                                }

                            }

                            Text("$fireRessis (90%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/block.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                            }

                            Text("$earthRessis (90%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/block.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                            }

                            Text("$airRessis (90%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/block.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                            }

                            Text("$waterRessis (90%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                //Dominios y resistencias secundarias
                Row (
                    modifier = Modifier.padding(10.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/melee_mastery.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("D. Melee", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$dominioMelee", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Criticos
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/critical_mastery.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("D. Critico", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$dominioCritico", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/rear_mastery.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("D. Espalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$dominioEspalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/healing_mastery.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("D. Curas", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$dominioCuras", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/armor_given.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Ar. Dada", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$armaduraDada", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/distance_mastery.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("D. Distancia", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$dominioDistancia", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/critical_resistance.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("R. Critica", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$resistenciaCritica", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/rear_resistance.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("R. Espalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$resitenciaEspalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/berserk_mastery.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("D. Berserk", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$dominioBerserk", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/armor_received.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Ar. Recibida", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$armaduraRecibida", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                //Estadisticas de combate
                Row (
                    modifier = Modifier.padding(10.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        //Daño infligido
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/damage_inflicted.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Daño Inf.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$dañoInfligido%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Prob. Critica
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/critical_hit.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Golpe Crit.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$probabilidadCritica%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        //Iniciativa
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/initiative.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("Iniciativa", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$iniciativa", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Esquiva
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/dodge.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Esquiva", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$esquiva", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Sabiduria
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/wisdom.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Sabiduria", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$sabiduria", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        //Control (de momento)
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/control.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Control", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$control", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/heals_performed.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("Cura Reali.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$curasRealizadas%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/block.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("Anticipacion", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$anticipacion%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Alcance
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/range.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("Alcance", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$alcance", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        //Placaje
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/lock.png",
                                        contentDescription = "fire resi"
                                    )
                                }
                                Text("Placaje", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("$placaje", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Prospeccion
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/prospecting.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Prospeccion", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$prospeccion", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        //Voluntad
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/statistics/force_of_will.png",
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Voluntad", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("$voluntad", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
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
                    state = lazyGridState,
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    modifier = Modifier.fillMaxHeight().weight(1f).background(color = panelColor, shape = RoundedCornerShape(10.dp)).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(itemPool,key = {it[Equipments.id]}) {
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