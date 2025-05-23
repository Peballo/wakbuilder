package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import itemSprite
import itemTypeSprite
import org.jetbrains.exposed.sql.ResultRow
import parseEffect
import rarityColors
import raritySprite


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

        modifier = Modifier.fillMaxSize()
            .defaultMinSize(minHeight = 360.dp)
            .fillMaxWidth()
            .height(360.dp),
        colors = ButtonDefaults.buttonColors(Color(171, 214, 250)),
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick(item) }
    ) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            // Name of the item
            Text(item[Equipments.name_es], fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = nameColor, style = TextStyle(shadow = Shadow(
                Color.Black, blurRadius = 1f)
            )
            )

            // Item sprite, level, type and rarity
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Box (
                    modifier = Modifier.padding(8.dp).background(Color(150, 190,250), RoundedCornerShape(8.dp))
                ) {
                    AsyncImage (
                        modifier = Modifier.size(60.dp),
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
