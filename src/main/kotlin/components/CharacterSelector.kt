import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

data class CharacterClass(
    val imageUrl: String,
    val className: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FixedGridImageSelector(
    selectedClass: CharacterClass,
    onClassSelected: (CharacterClass) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val classes = listOf(
        CharacterClass(
            characterAvatar("sacrier"),
            "sacrier"
        ),
        CharacterClass(
            characterAvatar("eniripsa"),
            "eniripsa"
        ),
        CharacterClass(
            characterAvatar("cra"),
            "cra"
        ),
        CharacterClass(
            characterAvatar("osamodas"),
            "osamodas"
        ),
        CharacterClass(
            characterAvatar("eliotrope"),
            "eliotrope"
        ),
        CharacterClass(
            characterAvatar("masqueraider"),
            "masqueraider"
        ),
        CharacterClass(
            characterAvatar("sadida"),
            "sadida"
        ),
        CharacterClass(
            characterAvatar("huppermage"),
            "huppermage"
        ),
        CharacterClass(
            characterAvatar("ecaflip"),
            "ecaflip"
        ),
        CharacterClass(
            characterAvatar("ouginak"),
            "ouginak"
        ),
        CharacterClass(
            characterAvatar("pandawa"),
            "pandawa"
        ),
        CharacterClass(
            characterAvatar("sram"),
            "sram"
        ),
        CharacterClass(
            characterAvatar("enutrof"),
            "enutrof"
        ),
        CharacterClass(
            characterAvatar("iop"),
            "iop"
        ),
        CharacterClass(
            characterAvatar("feca"),
            "feca"
        ),
        CharacterClass(
            characterAvatar("xelor"),
            "xelor"
        ),
        CharacterClass(
            characterAvatar("rogue"),
            "rogue"
        ),
        CharacterClass(
            characterAvatar("foggernaut"),
            "foggernaut"
        ),
        // Forjalanza coming soon
    )

    Box(modifier = modifier) {
        // Imagen principal seleccionada
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            model = selectedClass.imageUrl,
            contentDescription = "Seleccionar clase",
            contentScale = ContentScale.Fit
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = Color(202, 230, 255))
                .width(276.dp)  // (80dp * 3) + (8dp * 3) para tres columnas con padding
                .heightIn(max = 300.dp)  // Altura máxima fija
        ) {
            FlowRow(
                modifier = Modifier.padding(8.dp),
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classes.forEach { characterClass ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                onClassSelected(characterClass)
                                expanded = false
                            }
                    ) {
                        AsyncImage(
                            model = characterClass.imageUrl,
                            contentDescription = characterClass.className,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}