package components

import BuildItemsList
import CharacterStats
import Equipments
import FixedGridImageSelector
import Rarity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import checkLevelInput
import coil3.compose.AsyncImage
import getCharacterClassByName
import itemSprite
import calculateStats
import org.jetbrains.exposed.sql.ResultRow
import parseEquipmentToItemType
import repositories.*
import statSprite

@Composable
fun BuildWindow(buildCode: String, onRouteChanged: (String) -> Unit) {

    // Repositories AIVEN
    val er = EquipmentsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val ar = ActionsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val efr = EffectsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val sr = StatesRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val jr = JobsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))
    val br = BuildsRepository(envReader.getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/wakbuilder"), "org.postgresql.Driver", envReader.getOrDefault("DB_USER", "postgres"), envReader.getOrDefault("DB_PASSWORD", "1234"))

    val actions by remember { mutableStateOf(ar.getAllActions()) }
    val effects by remember { mutableStateOf(efr.getAllEffects()) }
    val jobs by remember { mutableStateOf(jr.getAllJobs()) }
    val states by remember { mutableStateOf(sr.getAllStates()) }

    val lazyGridState = rememberLazyGridState()

    // Colors
    val statPillColor: Color = Color(170, 196,230) // The color for the boxes where the HP, AP, MP and WP are
    val panelColor: Color = Color(202, 230, 255) // The color for each big box where data is shown
    var isBuildLoadedOnce by remember { mutableStateOf(false) }
    var buildRow by remember { mutableStateOf(br.getBuildByCode(buildCode)) }

    // States
    var itemPool: List<ResultRow> by remember { mutableStateOf(emptyList()) }
    var build by remember { mutableStateOf(BuildItemsList()) }
    var selectedClass by remember { mutableStateOf(getCharacterClassByName(buildRow!![Builds.character])) }
    var stats by remember { mutableStateOf(CharacterStats(level = buildRow!![Builds.level], hp= buildRow!![Builds.level]*10+50, ap=6, mp=3, wp=6, criticalChance=3, control=1, character = selectedClass.className , code = buildCode, account = buildRow!![Builds.user], name = buildRow!![Builds.name])) }
    var lastClickedBuildPart by remember { mutableStateOf("") }
    var selectedRanges by remember { mutableStateOf(setOf("")) }
    var selectedRarities by remember { mutableStateOf(setOf<Int>()) }

    if (!isBuildLoadedOnce) {
        isBuildLoadedOnce = true;

        if (buildRow != null) {
            build = BuildItemsList(
                helmet = er.getEquipmentById(buildRow!![Builds.helmet]),
                neck = er.getEquipmentById(buildRow!![Builds.neck]),
                chest = er.getEquipmentById(buildRow!![Builds.chest]),
                left_ring = er.getEquipmentById(buildRow!![Builds.left_ring]),
                right_ring = er.getEquipmentById(buildRow!![Builds.right_ring]),
                boots = er.getEquipmentById(buildRow!![Builds.boots]),
                cape = er.getEquipmentById(buildRow!![Builds.cape]),
                epaulettes = er.getEquipmentById(buildRow!![Builds.epaulettes]),
                belt = er.getEquipmentById(buildRow!![Builds.belt]),
                emblem = er.getEquipmentById(buildRow!![Builds.emblem]),
                first_weapon = er.getEquipmentById(buildRow!![Builds.first_weapon]),
                second_weapon = er.getEquipmentById(buildRow!![Builds.second_weapon]),
                mount = er.getEquipmentById(buildRow!![Builds.mount]),
                pet =  er.getEquipmentById(buildRow!![Builds.pet]),
            )

            stats = stats.copy(
                level = buildRow!![Builds.level],
                character = buildRow!![Builds.character],
                name = buildRow!![Builds.name],
                code = buildRow!![Builds.id],
                account = buildRow!![Builds.user],
            )

            stats = calculateStats(stats, build, effects, br, true);
        }
    }

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
                        Text(stats.name, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        TextField(
                            value = "${stats.level}",
                            onValueChange = { stats = stats.copy(level = checkLevelInput( it));stats = calculateStats(stats, build, effects, br) },
                            label = { Text("Nivel") },
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = statPillColor),
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

                        Text("${stats.hp}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                            if (stats.character == "huppermage")
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

                        Text("${if (stats.character == "huppermage") stats.wp * 75 else stats.wp}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                        lastClickedBuildPart = "helmet"
                        //itemPool = er.getEquipmentsByTypeAndLevel(134, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(134, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "neck"
//                        itemPool = er.getEquipmentsByTypeAndLevel(120, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(120, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "chest"
                        //itemPool = er.getEquipmentsByTypeAndLevel(136, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(136, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "left_ring"
                        //itemPool = er.getEquipmentsByTypeAndLevel(103, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(103, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "right_ring"
                        //itemPool = er.getEquipmentsByTypeAndLevel(103, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(103, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "boots"
                        //itemPool = er.getEquipmentsByTypeAndLevel(119, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(119, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "cape"
                       // itemPool = er.getEquipmentsByTypeAndLevel(132, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(132, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "epaulettes"
                        //itemPool = er.getEquipmentsByTypeAndLevel(138, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(138, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "belt"
                        //itemPool = er.getEquipmentsByTypeAndLevel(133, stats.level)
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(133, selectedRanges, selectedRarities)
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
                        lastClickedBuildPart = "first_weapon"
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(listOf(101, 108, 110, 111, 113, 114, 115, 117, 223, 253, 254), selectedRanges, selectedRarities)
                    }
                ) {
                    if (build.first_weapon != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = itemSprite(build.first_weapon!![Equipments.sprite_id]),
                            contentDescription = "primary weapon",
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
                        lastClickedBuildPart = "second_weapon"
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(listOf(112, 189), selectedRanges)
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
                            contentDescription = "second weapon",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

                Button (
                    modifier = Modifier.size(64.dp).background(statPillColor, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = statPillColor),
                    onClick = {
                        lastClickedBuildPart = "emblem"
                        itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(listOf(537, 646), selectedRanges)
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
                        lastClickedBuildPart = "mount"
                        itemPool = er.getEquipmentsByType(611)
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
                        lastClickedBuildPart = "pet"
                        itemPool = er.getEquipmentsByType(582)
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
                            when (lastClickedBuildPart) {
                                "helmet" -> {
                                    build = if (build.helmet != null) {
                                        if (build.helmet!![Equipments.id] == selectedItem[Equipments.id]) build.copy(helmet = null)
                                        else build.copy(helmet = selectedItem)
                                    } else build.copy(helmet = selectedItem)
                                }
                                "neck" -> {
                                    build = if (build.neck != null) {
                                        if (build.neck!![Equipments.id] == selectedItem[Equipments.id]) build.copy(neck = null)
                                        else build.copy(neck = selectedItem)
                                    } else build.copy(neck = selectedItem)
                                }
                                "chest" -> {
                                    build = if (build.chest != null) {
                                        if (build.chest!![Equipments.id] == selectedItem[Equipments.id]) build.copy(chest = null)
                                        else build.copy(chest = selectedItem)
                                    } else build.copy(chest = selectedItem)
                                }
                                "left_ring" -> {
                                    build = if (build.left_ring != null) {
                                        if (build.left_ring!![Equipments.id] == selectedItem[Equipments.id]) build.copy(left_ring = null)
                                        else build.copy(left_ring = selectedItem)
                                    } else build.copy(left_ring = selectedItem)
                                }
                                "right_ring" -> {
                                    build = if (build.right_ring != null) {
                                        if (build.right_ring!![Equipments.id] == selectedItem[Equipments.id]) build.copy(right_ring = null)
                                        else build.copy(right_ring = selectedItem)
                                    } else build.copy(right_ring = selectedItem)
                                }
                                "boots" -> {
                                    build = if (build.boots != null) {
                                        if (build.boots!![Equipments.id] == selectedItem[Equipments.id]) build.copy(boots = null)
                                        else build.copy(boots = selectedItem)
                                    } else build.copy(boots = selectedItem)
                                }
                                "cape" -> {
                                    build = if (build.cape != null) {
                                        if (build.cape!![Equipments.id] == selectedItem[Equipments.id]) build.copy(cape = null)
                                        else build.copy(cape = selectedItem)
                                    } else build.copy(cape = selectedItem)
                                }
                                "epaulettes" -> {
                                    build = if (build.epaulettes != null) {
                                        if (build.epaulettes!![Equipments.id] == selectedItem[Equipments.id]) build.copy(epaulettes = null)
                                        else build.copy(epaulettes = selectedItem)
                                    } else build.copy(epaulettes = selectedItem)
                                }
                                "belt" -> {
                                    build = if (build.belt != null) {
                                        if (build.belt!![Equipments.id] == selectedItem[Equipments.id]) build.copy(belt = null)
                                        else build.copy(belt = selectedItem)
                                    } else build.copy(belt = selectedItem)
                                }
                                "first_weapon" -> {
                                    build = if (build.first_weapon != null) {
                                        if (build.first_weapon!![Equipments.id] == selectedItem[Equipments.id]) build.copy(first_weapon = null)
                                        else build.copy(first_weapon = selectedItem)
                                    } else build.copy(first_weapon = selectedItem)
                                }
                                "second_weapon" -> {
                                    build = if (build.second_weapon != null) {
                                        if (build.second_weapon!![Equipments.id] == selectedItem[Equipments.id]) build.copy(second_weapon = null)
                                        else build.copy(second_weapon = selectedItem)
                                    } else build.copy(second_weapon = selectedItem)
                                }
                                "emblem" -> {
                                    build = if (build.emblem != null) {
                                        if (build.emblem!![Equipments.id] == selectedItem[Equipments.id]) build.copy(emblem = null)
                                        else build.copy(emblem = selectedItem)
                                    } else build.copy(emblem = selectedItem)
                                }
                                "mount" -> {
                                    build = if (build.mount != null) {
                                        if (build.mount!![Equipments.id] == selectedItem[Equipments.id]) build.copy(mount = null)
                                        else build.copy(mount = selectedItem)
                                    } else build.copy(mount = selectedItem)
                                }
                                "pet" -> {
                                    build = if (build.pet != null) {
                                        if (build.pet!![Equipments.id] == selectedItem[Equipments.id]) build.copy(pet = null)
                                        else build.copy(pet = selectedItem)
                                    } else build.copy(pet = selectedItem)
                                }
                                else -> {

                                }
                            }

                            stats = calculateStats(stats, build, effects, br)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(220.dp)
                        .background(color = panelColor, shape = RoundedCornerShape(10.dp))
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rangos de niveles:",
                            modifier = Modifier
                        )
                    }

                    val levelRanges = listOf(
                        "Todos",  // Añadido "Todos" al principio
                        "0 - 5", "6 - 20",
                        "21 - 35", "36 - 50",
                        "51 - 65", "66 - 80",
                        "81 - 95", "96 - 110",
                        "111 - 125", "126 - 140",
                        "141 - 155", "156 - 170",
                        "171 - 185", "186 - 200",
                        "201 - 215", "216 - 230",
                        "231 - 245"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(levelRanges) { range ->
                                if (range.isNotEmpty()) {  // Solo mostrar botón si el range no está vacío
                                    val isAllButton = range == "Todos"
                                    val isSelected = if (isAllButton) {
                                        selectedRanges.size == levelRanges.size - 2  // -2 por "Todos" y el elemento vacío
                                    } else {
                                        selectedRanges.contains(range)
                                    }

                                    Button(
                                        onClick = {
                                            if (isAllButton) {
                                                selectedRanges = if (selectedRanges.size == levelRanges.size - 2) {
                                                    emptySet()
                                                } else {
                                                    levelRanges.filter { it != "Todos" && it.isNotEmpty() }.toSet()
                                                }
                                            } else {
                                                selectedRanges = if (isSelected) {
                                                    selectedRanges - range
                                                } else {
                                                    selectedRanges + range
                                                }
                                            }
                                            if (lastClickedBuildPart.isNotEmpty()) {itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(parseEquipmentToItemType(lastClickedBuildPart), selectedRanges, selectedRarities) }
                                        },
                                        modifier = Modifier
                                            .width(85.dp)
                                            .height(24.dp),
                                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (isSelected) {
                                                Color(171, 214, 250)
                                            } else {
                                                Color(170, 196, 230)
                                            }
                                        ),
                                        shape = RoundedCornerShape(2.dp)
                                    ) {
                                        Text(
                                            text = range,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            color = Color.Black.copy(alpha = if (isSelected) 1f else 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val rarities = listOf(
                        Rarity(1, "Común", "https://tmktahu.github.io/WakfuAssets/rarities/1.png"),
                        Rarity(2, "Raro", "https://tmktahu.github.io/WakfuAssets/rarities/2.png"),
                        Rarity(3, "Mítico", "https://tmktahu.github.io/WakfuAssets/rarities/3.png"),
                        Rarity(4, "Legendario", "https://tmktahu.github.io/WakfuAssets/rarities/4.png"),
                        Rarity(5, "Reliquia", "https://tmktahu.github.io/WakfuAssets/rarities/5.png"),
                        Rarity(6, "Recuerdo", "https://tmktahu.github.io/WakfuAssets/rarities/6.png"),
                        Rarity(7, "Épico", "https://tmktahu.github.io/WakfuAssets/rarities/7.png")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rarezas:",
                            modifier = Modifier
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp), // Reducido el padding vertical
                        horizontalArrangement = Arrangement.spacedBy(4.dp), // Reducido el espacio entre botones
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rarities.forEach { rarity ->
                            Button(
                                onClick = {
                                    selectedRarities = if (rarity.id in selectedRarities) {
                                        selectedRarities - rarity.id
                                    } else {
                                        selectedRarities + rarity.id
                                    }
                                    if (lastClickedBuildPart.isNotEmpty()) {itemPool = er.getEquipmentsByTypeAndMultipleLevelRange(parseEquipmentToItemType(lastClickedBuildPart), selectedRanges, selectedRarities) }
                                },
                                modifier = Modifier
                                    .height(24.dp) // Reducido de 32dp a 24dp
                                    .width(24.dp), // Reducido de 32dp a 24dp
                                contentPadding = PaddingValues(2.dp), // Reducido el padding interno
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (rarity.id in selectedRarities) {
                                        Color(171, 214, 250)
                                    } else {
                                        Color(170, 196, 230)
                                    }
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                AsyncImage(
                                    model = rarity.imageUrl,
                                    contentDescription = rarity.name,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}