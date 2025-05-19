import androidx.compose.ui.graphics.Color
import org.jetbrains.exposed.sql.ResultRow
import java.io.File
import kotlin.math.floor

object envReader {
    private val envMap = mutableMapOf<String, String>()
    init {
        File("src/.env").readLines().forEach {
            line -> if(line.isNotBlank() && !line.startsWith("#")) {
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    envMap[key] = value
                }
            }
        }
    }
    operator fun get(key: String) : String? = envMap[key]
    fun getOrDefault(key: String, default: String) : String = envMap[key] ?: default
}

data class Condition (val name: String, val value: Regex)

fun firstParam(params: List<Float>, level: Int): Int {
    return (params[0] + params[1] * level).toInt()
}

fun secondParam(params: List<Float>, level: Int): Int {
    return (params[2] + params[3] * level).toInt()
}

fun thirdParam(params: List<Float>, level: Int): Int {
    return (params[4] + params[5] * level).toInt()
}

// [>2]
fun isLastStackValueGreaterThanTwo(stack: Int): Boolean {
    return stack >= 2
}

// [>2]?s:
fun plural(stack: Int): String {
    return if (isLastStackValueGreaterThanTwo(stack)) "s" else ""
}

fun characterAvatar(character: String): String = "https://tmktahu.github.io/WakfuAssets/classes/$character.png"

fun raritySprite(rarity: Int): String = "https://vertylo.github.io/wakassets/rarities/$rarity.png"

fun itemTypeSprite(type: Int): String = "https://tmktahu.github.io/WakfuAssets/itemTypes/$type.png"

fun itemSprite(id: Int): String = "https://vertylo.github.io/wakassets/items/$id.png"

fun statSprite(stat: String): String = "https://tmktahu.github.io/WakfuAssets/statistics/$stat.png"

fun detectFirstCondition(desc: String): String {
    val conditions = listOf(
        Condition("{[~3]?", Regex("\\{\\[~3]\\?")),
        Condition("{[~2]?", Regex("\\{\\[~2]\\?")),
        Condition("{[>2]?s:}", Regex("\\{\\[>2]\\?s:}")),
        Condition("[#1]", Regex("\\[#1]")),
        Condition("[#2]", Regex("\\[#2]")),
        Condition("[#3]", Regex("\\[#3]"))
    )

    var firstCondition: String = ""
    var firstIndex = desc.length

    conditions.forEach {
            condition ->
        val match = condition.value.find(desc)
        if (match != null && match.range.first < firstIndex) {
            firstIndex = match.range.first
            firstCondition = condition.name
        }

    }

    return firstCondition
}

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

fun parseEffect(effect: ResultRow, level: Int, actions: List<ResultRow>, states: List<ResultRow>, jobs: List<ResultRow>): String {

    var result = "Not found"

    val action: ResultRow? = actions.find { action -> action[Actions.id] == effect[Effects.action] }

    if (action != null) {
        result = action[Actions.desc_es]
        val params: List<Float> = effect[Effects.params]

        when (action[Actions.id]) {
            304 -> {
                val state = states.find { s -> s[States.id] == params[0].toInt()}

                if (state != null) {
                    return state[States.name_es]
                }
            }
            39 -> println("Algo raro")
            2001 -> println("Profesión")
            else -> {
                var desc: String = action[Actions.desc_es]

                var stack: Int = 0
                val hasThreeOrMoreArguments: Boolean = params.size >= 6 // [~3]
                var computedParamNotFound: Boolean = true

                while (computedParamNotFound) {
                    computedParamNotFound = false

                    var firstCondition: String = detectFirstCondition(desc)

                    if (firstCondition.isNotEmpty()) {
                        computedParamNotFound = true

                        if (firstCondition == "{[~3]?") {
                            if (!hasThreeOrMoreArguments) {
                                desc = desc.substring(desc.indexOf(":")+1, desc.length-1)
                            }
                        } else if (firstCondition == "{[~2]?") {
                            desc = desc.replace(firstCondition, " ")
                            desc = desc.replace(":}", "")
                        } else if (firstCondition == "{[>2]?s:}") {
                            desc = desc.replace(firstCondition, plural(stack))
                        } else if (firstCondition == "[#1]") {
                            desc = desc.replace(firstCondition, "${floor(firstParam(params, level).toDouble()).toInt()}")
                        } else if (firstCondition == "[#2]") {
                            if (action[Actions.id] == 2001) desc = desc.replace(firstCondition, "${params[2]}")
                            else desc = desc.replace(firstCondition, "${floor(secondParam(params, level).toDouble()).toInt()}")
                        } else if (firstCondition == "[#3]") {
                            desc = desc.replace(firstCondition, "${floor(thirdParam(params, level).toDouble()).toInt()}")
                        }
                    }
                }

                desc = desc.replace("[el1]", "fuego")
                desc = desc.replace("[el2]", "agua")
                desc = desc.replace("[el3]", "tierra")
                desc = desc.replace("[el4]", "aire")

                result = desc
            }
        }
    }

    return result
}
