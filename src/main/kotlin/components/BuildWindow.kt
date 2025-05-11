package components

import ActionsRepository
import CharacterClass
import EffectsRepository
import EquipmentsRepository
import FixedGridImageSelector
import JobsRepository
import StatesRepository
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
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import checkLevelInput
import coil3.compose.AsyncImage
import itemSprite
import itemTypeSprite
import org.jetbrains.exposed.sql.ResultRow
import parseEffect
import rarityColors
import raritySprite
import statSprite

data class BuildItemsList (
    var helmet: ResultRow? = null,
    var neck: ResultRow? = null,
    var chest: ResultRow? = null,
    var left_ring: ResultRow? = null,
    var right_ring: ResultRow? = null,
    var boots: ResultRow? = null,
    var cape: ResultRow? = null,
    var epaulettes: ResultRow? = null,
    var belt: ResultRow? = null,
    var first_weapon: ResultRow? = null,
    var second_weapon: ResultRow? = null,
    var mount: ResultRow? = null,
    var pet: ResultRow? = null,
    var emblem: ResultRow? = null
)

data class CharacterStats (
    var level: Int = 1,
    var ap: Int = 6,
    var mp: Int = 3,
    var wp: Int = 6,
    var fireA: Int = 1000,
    var fireR: Int = 9999,
    var earthA: Int = 1,
    var earthR: Int = 9999,
    var waterA: Int = 2,
    var waterR: Int = 9999,
    var airA: Int = 3,
    var airR: Int = 9999,
    var melee: Int = 1,
    var distance: Int = 300,
    var critical: Int = 31,
    var rear: Int = 400,
    var berserk: Int = 69,
    var healing: Int = 0,
    var givenArmour: Int = 0,
    var receivedArmour: Int = 0,
    var criticalResistance: Int = 0,
    var rearResistance: Int = 0,
    var inflictedDmg: Int = 0,
    var criticalChance: Int = 3,
    var heals: Int = 0,
    var block: Int = 0,
    var initiative: Int = 0,
    var lock: Int = 0,
    var dodge: Int = 0,
    var will: Int = 0,
    var control: Int = 1,
    var range: Int = 0,
    var prospection: Int = 0,
    var wisdom: Int = 0,
    var character: String = "sacrier"
)



@Composable
fun LeftOrRightRing(
    showDialog: Boolean,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Elegir anillo")
            },
            text = {
                Text("¿Se va a equipar al anillo izquierdo o derecho?")
            },
            confirmButton = {
                TextButton(onClick = onRight) {
                    Text("Derecho")
                }
            },
            dismissButton = {
                TextButton(onClick = onLeft) {
                    Text("Izquierdo")
                }
            }
        )
    }
}

@Composable
fun EquipmentCell(item: ResultRow, effects: List<ResultRow>, actions: List<ResultRow>, states: List<ResultRow>, jobs: List<ResultRow>, onClick: (ResultRow) -> Unit) {
    val nameColor: Color = rarityColors(item[Equipments.rarity])

    var effs: List<ResultRow> = emptyList()

    var parsedEffects: List<String> = emptyList()

    for ( effect in item[Equipments.effects]) {
        var e = effects.find { eff -> eff[Effects.id] == effect }

        if (e != null) {
            effs = effs.plus(e)
        }
    }

    if (effs.isNotEmpty()) {
        effs.sortedBy {
            it[Effects.action]
        }

        for (ef in effs) {
            parsedEffects = parsedEffects.plus(parseEffect(ef, item[Equipments.level], actions, states, jobs))
        }
    }

    Button(
        modifier = Modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(Color(171, 214, 250)),
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick(item) }
    ) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            // Name of the item
            Text(item[Equipments.name_es], fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = nameColor, style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 1f)))

            // Item sprite, level, type and rarity
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Box (
                    modifier = Modifier.padding(8.dp).background(Color(150, 190,250), RoundedCornerShape(8.dp))
                ) {
                    AsyncImage (
                        model = itemSprite(item[Equipments.sprite_id]),
                        contentDescription = "sprite"
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
                            model = itemTypeSprite(item[Equipments.item_type]),
                            contentDescription = "type"
                        )
                        AsyncImage(
                            modifier = Modifier.size(20.dp),
                            model = raritySprite(item[Equipments.rarity]),
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
    val ar: ActionsRepository = ActionsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val efr: EffectsRepository = EffectsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val sr: StatesRepository = StatesRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    val jr: JobsRepository = JobsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")


    val actions by remember { mutableStateOf(ar.getAllActions()) }
    val effects by remember { mutableStateOf(efr.getAllEffects()) }
    val jobs by remember { mutableStateOf(jr.getAllJobs()) }
    val states by remember { mutableStateOf(sr.getAllStates()) }

    val lazyGridState = rememberLazyGridState()

    // Colors
    val statPillColor: Color = Color(170, 196,230) // The color for the boxes where the HP, AP, MP and WP are
    val panelColor: Color = Color(202, 230, 255) // The color for each big box where data is shown

    // States
    var itemPool: List<ResultRow> by remember { mutableStateOf(emptyList()) }
    var effectsPool: List<ResultRow> by remember { mutableStateOf(emptyList()) }
    var build by remember { mutableStateOf(BuildItemsList()) }
    var stats by remember { mutableStateOf(CharacterStats()) }
    var showLeftRingDialog by remember { mutableStateOf(false) }
    var lastClickedEquipment by remember { mutableStateOf<ResultRow?>(null) }
    var lastClickedBuildPart by remember { mutableStateOf("") }

    var selectedClass by remember {
        mutableStateOf(
            CharacterClass(
                "https://raw.githubusercontent.com/Tmktahu/WakfuAssets/refs/heads/main/classes/sacrier.png",
                "Sacrier"
            )
        )
    }

    LeftOrRightRing(
        showLeftRingDialog,
        onRight = {
            if (lastClickedEquipment != null && lastClickedEquipment!![Equipments.item_type] == 103) {
                build = build.copy(
                    right_ring = lastClickedEquipment
                )
            }
            showLeftRingDialog = false
        },
        onLeft = {
            if (lastClickedEquipment != null && lastClickedEquipment!![Equipments.item_type] == 103) {
                build = build.copy(
                    left_ring = lastClickedEquipment
                )
            }
            showLeftRingDialog = false
        },
        onDismiss = {
            showLeftRingDialog = false
        }
    )

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
                    // Nuevo selector de imágenes en grid
                    FixedGridImageSelector(
                        selectedClass = selectedClass,
                        onClassSelected = { newClass ->
                            selectedClass = newClass
                            stats = stats.copy(character = selectedClass.className)
                        }
                    )

                    Column {
                        Text("Visama", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        TextField(
                            value = "${stats.level}",
                            onValueChange = { stats = stats.copy(level = checkLevelInput( it)) },
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
                                model = statSprite("empty_coin"),
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = statSprite("health_points"),
                                contentDescription = "hp"
                            )
                        }

                        Text("${stats.level*10+50}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = statSprite("empty_coin"),
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = statSprite("action_points"),
                                contentDescription = "ap"
                            )
                        }

                        Text("${stats.ap}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = statSprite("empty_coin"),
                                contentDescription = "coin"
                            )
                            AsyncImage(
                                modifier = Modifier.padding(8.dp),
                                model = statSprite("movement_points"),
                                contentDescription = "mp"
                            )
                        }

                        Text("${stats.mp}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Column (
                        modifier = Modifier.weight(1f).background(color = statPillColor, shape = RoundedCornerShape(10.dp)).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box {
                            AsyncImage(
                                model = statSprite("empty_coin"),
                                contentDescription = "coin"
                            )
                            if (stats.character == "Hipermago")
                                AsyncImage(
                                    modifier = Modifier.padding(6.dp),
                                    model = statSprite("quadrumental_breeze"),
                                    contentDescription = "wp"
                                )
                            else
                                AsyncImage(
                                    modifier = Modifier.padding(8.dp),
                                    model = statSprite("wakfu_points"),
                                    contentDescription = "wp"
                                )
                        }

                        Text("${if (stats.character == "Hipermago") stats.wp * 75 else stats.wp}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.width(65.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = statSprite("fire_coin"),
                                        contentDescription = "fire dmg"
                                    )
                                }
                                Text("Fuego:", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                            }
                            Text("${stats.fireA}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Tierra
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.width(65.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = statSprite("earth_coin"),
                                        contentDescription = "earth dmg"
                                    )
                                }
                                Text("Tierra:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text("${stats.earthA}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Aire
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.width(65.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = statSprite("air_coin"),
                                        contentDescription = "air dmg"
                                    )
                                }
                                Text("Aire:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text("${stats.airA}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        //Water
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.width(65.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        modifier = Modifier.size(20.dp),
                                        model = statSprite("water_coin"),
                                        contentDescription = "water dmg"
                                    )
                                }
                                Text("Agua:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.waterA}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

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
                                        model = statSprite("block"),
                                        contentDescription = "fire resi"
                                    )
                                }

                            }

                            Text("${stats.fireR} (${Math.clamp((1 - Math.pow(0.8, (stats.fireR/100.0)))*100, 0.0, 90.0).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

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
                                        model = statSprite("block"),
                                        contentDescription = "earth resi"
                                    )
                                }
                            }

                            Text("${stats.earthR} (${Math.clamp((1 - Math.pow(0.8, (stats.earthR/100.0)))*100, 0.0, 90.0).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

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
                                        model = statSprite("block"),
                                        contentDescription = "air resi"
                                    )
                                }
                            }

                            Text("${stats.airR} (${Math.clamp((1 - Math.pow(0.8, (stats.airR/100.0)))*100, 0.0, 90.0).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

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
                                        model = statSprite("block"),
                                        contentDescription = "water resi"
                                    )
                                }
                            }

                            Text("${stats.waterR} (${Math.clamp((1 - Math.pow(0.8, (stats.waterR/100.0)))*100, 0.0, 90.0).toInt()}%)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Row (
                    modifier = Modifier.padding(10.dp).background(color = statPillColor, shape = RoundedCornerShape(10.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.weight(1f).padding(8.dp)
                    ) {
                        Text("Daños totales: ${Math.max(Math.max(stats.fireA, stats.earthA),Math.max(stats.airA, stats.waterA))+stats.critical+stats.berserk+stats.rear+stats.melee+stats.distance}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("melee_mastery"),
                                        contentDescription = "melee dmg"
                                    )
                                }
                                Text("D. Melee", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.melee}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("critical_mastery"),
                                        contentDescription = "crit dmg"
                                    )
                                }
                                Text("D. Critico", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.critical}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("rear_mastery"),
                                        contentDescription = "rear dmg"
                                    )
                                }
                                Text("D. Espalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.rear}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("healing_mastery"),
                                        contentDescription = "healing dmg"
                                    )
                                }
                                Text("D. Curas", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.healing}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("armor_given"),
                                        contentDescription = "armour g"
                                    )
                                }
                                Text("Ar. Dada", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.givenArmour}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("distance_mastery"),
                                        contentDescription = "distance dmg"
                                    )
                                }
                                Text("D. Distancia", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.distance}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("critical_resistance"),
                                        contentDescription = "critcal res"
                                    )
                                }
                                Text("R. Critica", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.criticalResistance}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("rear_resistance"),
                                        contentDescription = "rear res"
                                    )
                                }
                                Text("R. Espalda", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.rearResistance}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("berserk_mastery"),
                                        contentDescription = "berserk dmg"
                                    )
                                }
                                Text("D. Berserk", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.berserk}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("armor_received"),
                                        contentDescription = "armour r"
                                    )
                                }
                                Text("Ar. Recibida", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.receivedArmour}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("damage_inflicted"),
                                        contentDescription = "dmg"
                                    )
                                }
                                Text("Daño Inf.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.inflictedDmg}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("critical_hit"),
                                        contentDescription = "critical chance"
                                    )
                                }
                                Text("Golpe Crit.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.criticalChance}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("initiative"),
                                        contentDescription = "ini"
                                    )
                                }
                                Text("Iniciativa", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.initiative}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("dodge"),
                                        contentDescription = "dodge"
                                    )
                                }
                                Text("Esquiva", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.dodge}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("wisdom"),
                                        contentDescription = "wisdom"
                                    )
                                }
                                Text("Sabiduria", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.wisdom}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("control"),
                                        contentDescription = "control"
                                    )
                                }
                                Text("Control", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.control}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("heals_performed"),
                                        contentDescription = "heals"
                                    )
                                }
                                Text("Cura Reali.", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.heals}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("block"),
                                        contentDescription = "block"
                                    )
                                }
                                Text("Anticipacion", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.block}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("range"),
                                        contentDescription = "range"
                                    )
                                }
                                Text("Alcance", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.range}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("lock"),
                                        contentDescription = "lock"
                                    )
                                }
                                Text("Placaje", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Text("${stats.lock}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("prospecting"),
                                        contentDescription = "prospec"
                                    )
                                }
                                Text("Prospeccion", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.prospection}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                                        model = statSprite("force_of_will"),
                                        contentDescription = "will"
                                    )
                                }
                                Text("Voluntad", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }


                            Text("${stats.will}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                        itemPool = er.getEquipmentsByTypeAndLevel(134, stats.level)
                    }
                ) {
                    if (build.helmet != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.helmet!![Equipments.sprite_id]),
                            contentDescription = "helmet",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/HEAD.png",
                            contentDescription = "helmet",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(120, stats.level)
                    }
                ) {
                    if (build.neck != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.neck!![Equipments.sprite_id]),
                            contentDescription = "necklace",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/NECK.png",
                            contentDescription = "necklace",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(136, stats.level)
                    }
                ) {
                    if (build.chest != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.chest!![Equipments.sprite_id]),
                            contentDescription = "breastplate",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/CHEST.png",
                            contentDescription = "breastplate",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(103, stats.level)
                    }
                ) {
                    if (build.left_ring != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.left_ring!![Equipments.sprite_id]),
                            contentDescription = "left ring",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEFT_HAND.png",
                            contentDescription = "left ring",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }

                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(103, stats.level)
                    }
                ) {
                    if (build.right_ring != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.right_ring!![Equipments.sprite_id]),
                            contentDescription = "right ring",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/RIGHT_HAND.png",
                            contentDescription = "right ring",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(119, stats.level)
                    }
                ) {
                    if (build.boots != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.boots!![Equipments.sprite_id]),
                            contentDescription = "boots",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEGS.png",
                            contentDescription = "boots",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(132, stats.level)
                    }
                ) {
                    if (build.cape != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.cape!![Equipments.sprite_id]),
                            contentDescription = "cape",
                            alignment = Alignment.Center,
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BACK.png",
                            contentDescription = "cape",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(138, stats.level)
                    }
                ) {
                    if (build.epaulettes != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.epaulettes!![Equipments.sprite_id]),
                            contentDescription = "epaulettes",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SHOULDERS.png",
                            contentDescription = "epaulettes",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(133, stats.level)
                    }
                ) {
                    if (build.belt != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.belt!![Equipments.sprite_id]),
                            contentDescription = "belt",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BELT.png",
                            contentDescription = "belt",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypesAndLevel(listOf(101, 108, 110, 111, 113, 114, 115, 117, 223, 253, 254), stats.level)
                    }
                ) {
                    if (build.first_weapon != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.first_weapon!![Equipments.sprite_id]),
                            contentDescription = "belt",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/FIRST_WEAPON.png",
                            contentDescription = "primary weapon",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypesAndLevel(listOf(112, 189), stats.level)
                    }
                ) {
                    if (build.second_weapon != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.second_weapon!![Equipments.sprite_id]),
                            contentDescription = "second weapon",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SECOND_WEAPON.png",
                            contentDescription = "secondary weapon",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypesAndLevel(listOf(537, 646), stats.level)
                    }
                ) {
                    if (build.emblem != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.emblem!![Equipments.sprite_id]),
                            contentDescription = "emblem",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/ACCESSORY.png",
                            contentDescription = "emblem",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(582, 50)
                    }
                ) {
                    if (build.mount != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.mount!![Equipments.sprite_id]),
                            contentDescription = "mount",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/PET.png",
                            contentDescription = "mount",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        itemPool = er.getEquipmentsByTypeAndLevel(582, 50)
                    }
                ) {
                    if (build.pet != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.pet!![Equipments.sprite_id]),
                            contentDescription = "pet",
                            alignment = Alignment.Center
                        )
                    } else {
                        AsyncImage(
                            model = "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/PET.png",
                            contentDescription = "pet",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
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
                    modifier = Modifier.weight(1f).fillMaxHeight().background(color = panelColor, shape = RoundedCornerShape(10.dp)).padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(itemPool, key = {it[Equipments.id]}) { item ->
                        EquipmentCell(item, effects, actions, states, jobs) { selectedItem ->

                            lastClickedEquipment = selectedItem
                            if (selectedItem[Equipments.item_type] == 103) showLeftRingDialog = true

                            if (selectedItem[Equipments.item_type] == 582) {
                                if (lastClickedBuildPart.equals("mount")) build = build.copy(mount = selectedItem)
                                else if (lastClickedBuildPart.equals("pet")) build = build.copy(pet = selectedItem)
                            }

                            // ITEM_TYPE_ID EMBLEMS ARE 537, 646

                            build = build.copy(
                                helmet = if (selectedItem[Equipments.item_type] == 134) selectedItem else build.helmet,
                                neck = if (selectedItem[Equipments.item_type] == 120) selectedItem else build.neck,
                                chest = if (selectedItem[Equipments.item_type] == 136) selectedItem else build.chest,
                                left_ring = build.left_ring,
                                right_ring = build.right_ring,
                                boots = if (selectedItem[Equipments.item_type] == 119) selectedItem else build.boots,
                                cape = if (selectedItem[Equipments.item_type] == 132) selectedItem else build.cape,
                                epaulettes = if (selectedItem[Equipments.item_type] == 138) selectedItem else build.epaulettes,
                                belt = if (selectedItem[Equipments.item_type] == 133) selectedItem else build.belt,
                                first_weapon = build.first_weapon,
                                second_weapon = build.second_weapon,
                                mount = build.mount,
                                pet = build.pet,
                                emblem = if (selectedItem[Equipments.item_type] in listOf(537, 646)) selectedItem else build.emblem
                            )

                            if (build.helmet != null) {
                                // TODO: MAKE THIS HELMET CHANGE THE STATS BASED ON THE EFFECTS
                            }
                        }
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