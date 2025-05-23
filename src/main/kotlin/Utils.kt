import androidx.compose.ui.graphics.Color
import objects.Accounts
import org.jetbrains.exposed.sql.ResultRow
import repositories.AccountsRepository
import repositories.BuildsRepository
import java.io.File
import java.security.MessageDigest
import kotlin.math.floor
import kotlin.reflect.full.memberProperties

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

data class CharacterStats(
    var code: String = "",
    var name: String = "",
    var account: Int = -1,
    var level: Int = 1,
    var ap: Int = 0,
    var mp: Int = 0,
    var wp: Int = 0,
    var fireA: Int = 0,
    var fireR: Int = 0,
    var earthA: Int = 0,
    var earthR: Int = 0,
    var waterA: Int = 0,
    var waterR: Int = 0,
    var airA: Int = 0,
    var airR: Int = 0,
    var melee: Int = 0,
    var distance: Int = 0,
    var critical: Int = 0,
    var rear: Int = 0,
    var berserk: Int = 0,
    var healing: Int = 0,
    var givenArmour: Int = 0,
    var receivedArmour: Int = 0,
    var criticalResistance: Int = 0,
    var rearResistance: Int = 0,
    var inflictedDmg: Int = 0,
    var criticalChance: Int = 0,
    var heals: Int = 0,
    var block: Int = 0,
    var initiative: Int = 0,
    var lock: Int = 0,
    var dodge: Int = 0,
    var will: Int = 0,
    var control: Int = 0,
    var range: Int = 0,
    var prospection: Int = 0,
    var wisdom: Int = 0,
    var character: String = "sacrier",
    var hp: Int = 0
)

data class Rarity(
    val id: Int,
    val name: String,
    val imageUrl: String
)

fun getCharacterClassByName(character: String): CharacterClass {
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

    return classes.find { c -> c.className == character}!!
}

fun calculateStats(stats: CharacterStats, build: BuildItemsList, allEffects: List<ResultRow>, br: BuildsRepository, isLoadingBuild: Boolean = false): CharacterStats {
    var newStats = CharacterStats(level = stats.level, hp = stats.level*10+50, ap = 6, mp = 3, wp = 6, criticalChance = 3, control = 1, code = stats.code, name = stats.name, character = stats.character, account = stats.account )

    BuildItemsList::class.memberProperties.forEach { prop ->
        val item = prop.get(build) as ResultRow?

        if (item != null) {
            var effects = allEffects.filter { e -> e[Effects.id] in item[Equipments.effects]}

            for (effect in effects) {
                var params = effect[Effects.params]
                var level = item[Equipments.level]

                var firstP = 0
                var secondP = 0

                if (params.isNotEmpty()) {
                    firstP = firstParam(params, level)
                }

                if (params.size > 2) {
                    secondP = secondParam(params, level)
                }

                when (effect[Effects.action]) {
                    20 -> {
                        newStats = newStats.copy(hp = newStats.hp + firstP)
                    }
                    21 -> {
                        newStats = newStats.copy(hp = newStats.hp - firstP)
                    }
                    26 -> {
                        newStats = newStats.copy(healing = newStats.healing + firstP)
                    }
                    31 -> {
                        newStats = newStats.copy(ap = newStats.ap + firstP)
                    }
                    41 -> {
                        newStats = newStats.copy(mp = newStats.mp + firstP)
                    }
                    56 -> {
                        newStats = newStats.copy(ap = newStats.ap - firstP)
                    }
                    57 -> {
                        newStats = newStats.copy(mp = newStats.mp - firstP)
                    }
                    71 -> {
                        newStats = newStats.copy(rearResistance = newStats.rearResistance + firstP)
                    }
                    80 -> {
                        newStats = newStats.copy(
                            fireR = newStats.fireR + firstP,
                            earthR = newStats.earthR + firstP,
                            airR = newStats.airR + firstP,
                            waterR = newStats.waterR + firstP)
                    }
                    82 -> {
                        newStats = newStats.copy(fireR = newStats.fireR + firstP)
                    }
                    83 -> {
                        newStats = newStats.copy(waterR = newStats.waterR + firstP)
                    }
                    84 -> {
                        newStats = newStats.copy(earthR = newStats.earthR + firstP)
                    }
                    85 -> {
                        newStats = newStats.copy(airR = newStats.airR + firstP)
                    }
                    90, 100 -> {
                        newStats = newStats.copy(
                            fireR = newStats.fireR - firstP,
                            earthR = newStats.earthR - firstP,
                            airR = newStats.airR - firstP,
                            waterR = newStats.waterR - firstP)
                    }
                    97 -> {
                        newStats = newStats.copy(fireR = newStats.fireR - firstP)
                    }
                    98 -> {
                        newStats = newStats.copy(waterR = newStats.waterR - firstP)
                    }
                    96 -> {
                        newStats = newStats.copy(earthR = newStats.earthR - firstP)
                    }
                    120 -> {
                        newStats = newStats.copy(
                            fireA = newStats.fireA + firstP,
                            earthA = newStats.earthA + firstP,
                            airA = newStats.airA + firstP,
                            waterA = newStats.waterA + firstP)
                    }
                    122 -> {
                        newStats = newStats.copy(fireA = newStats.fireA + firstP)
                    }
                    124 -> {
                        newStats = newStats.copy(waterA = newStats.waterA + firstP)
                    }
                    123 -> {
                        newStats = newStats.copy(earthA = newStats.earthA + firstP)
                    }
                    125 -> {
                        newStats = newStats.copy(airA = newStats.airA + firstP)
                    }
                    130 -> {
                        newStats = newStats.copy(
                            fireA = newStats.fireA - firstP,
                            earthA = newStats.earthA - firstP,
                            airA = newStats.airA - firstP,
                            waterA = newStats.waterA - firstP)
                    }
                    132 -> {
                        newStats = newStats.copy(fireA = newStats.fireA - firstP)
                    }
                    149 -> {
                        newStats = newStats.copy(critical = newStats.critical + firstP)
                    }
                    150 -> {
                        newStats = newStats.copy(criticalChance = newStats.criticalChance + firstP)
                    }
                    160 -> {
                        newStats = newStats.copy(range = newStats.range + firstP)
                    }
                    161 -> {
                        newStats = newStats.copy(range = newStats.range - firstP)
                    }
                    162 -> {
                        newStats = newStats.copy(prospection = newStats.prospection + firstP)
                    }
                    166 -> {
                        newStats = newStats.copy(wisdom = newStats.wisdom + firstP)
                    }
                    168 -> {
                        newStats = newStats.copy(criticalChance = newStats.criticalChance - firstP)
                    }
                    171 -> {
                        newStats = newStats.copy(initiative = newStats.initiative + firstP)
                    }
                    172 -> {
                        newStats = newStats.copy(initiative = newStats.initiative - firstP)
                    }
                    173 -> {
                        newStats = newStats.copy(lock = newStats.lock + firstP)
                    }
                    174 -> {
                        newStats = newStats.copy(lock = newStats.lock - firstP)
                    }
                    175 -> {
                        newStats = newStats.copy(dodge = newStats.dodge + firstP)
                    }
                    176 -> {
                        newStats = newStats.copy(dodge = newStats.dodge - firstP)
                    }
                    177 -> {
                        newStats = newStats.copy(will = newStats.will + firstP)
                    }
                    180 -> {
                        newStats = newStats.copy(rear = newStats.rear + firstP)
                    }
                    181 -> {
                        newStats = newStats.copy(rear = newStats.rear - firstP)
                    }
                    184 -> {
                        newStats = newStats.copy(control = newStats.control + firstP)
                    }
                    191,193 -> {
                        newStats = newStats.copy(wp = newStats.wp + firstP)
                    }
                    192,194 -> {
                        newStats = newStats.copy(wp = newStats.wp - firstP)
                    }
                    875 -> {
                        newStats = newStats.copy(block = newStats.block + firstP)
                    }
                    876 -> {
                        newStats = newStats.copy(block = newStats.block - firstP)
                    }
                    988 -> {
                        newStats = newStats.copy(criticalResistance = newStats.criticalResistance + firstP)
                    }
                    1052 -> {
                        newStats = newStats.copy(melee = newStats.melee + firstP)
                    }
                    1053 -> {
                        newStats = newStats.copy(distance = newStats.distance + firstP)
                    }
                    1055 -> {
                        newStats = newStats.copy(berserk = newStats.berserk + firstP)
                    }
                    1056 -> {
                        newStats = newStats.copy(critical = newStats.critical - firstP)
                    }
                    1059 -> {
                        newStats = newStats.copy(melee = newStats.melee - firstP)
                    }
                    1060 -> {
                        newStats = newStats.copy(distance = newStats.distance - firstP)
                    }
                    1061 -> {
                        newStats = newStats.copy(berserk = newStats.berserk - firstP)
                    }
                    1062 -> {
                        newStats = newStats.copy(criticalResistance = newStats.criticalResistance - firstP)
                    }
                    1063 -> {
                        newStats = newStats.copy(rearResistance = newStats.rearResistance - firstP)
                    }
                    1068 -> {
                        if (secondP == 1) {
                            newStats = newStats.copy(fireA = newStats.fireA + firstP)
                        } else if (secondP == 2) {
                            newStats = newStats.copy(fireA = newStats.fireA + firstP, earthA = newStats.earthA + firstP)
                        } else {
                            newStats = newStats.copy(fireA = newStats.fireA + firstP, earthA = newStats.earthA + firstP, waterA = newStats.waterA + firstP)
                        }
                    }
                    1069 -> {
                        if (secondP == 1) {
                            newStats = newStats.copy(fireR = newStats.fireR + firstP)
                        } else if (secondP == 2) {
                            newStats = newStats.copy(fireR = newStats.fireR + firstP, earthR = newStats.earthR + firstP)
                        } else {
                            newStats = newStats.copy(fireR = newStats.fireR + firstP, earthR = newStats.earthR + firstP, waterR = newStats.waterR + firstP)
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }
    if (!isLoadingBuild) { br.updateBuild(stats, build) }
    return newStats
}



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
    val pattern = Regex("^\\d+$")

    if (levelLabel.isEmpty()) return 1
    if (levelLabel.matches(pattern)) {
        val level: Int = levelLabel.toInt()
        return if (level in 1..245) level
        else {
            if (level <= 0) 1
            else 245
        }
    }

    return 1
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

fun parseEquipmentToItemType(equipment : String) : List<Int> {
    return when (equipment) {
        "helmet" -> listOf(134)
        "neck" -> listOf(120)
        "chest" -> listOf(136)
        "left_ring", "right_ring" -> listOf(103)
        "boots" -> listOf(119)
        "cape" -> listOf(132)
        "epaulettes" -> listOf(138)
        "belt" -> listOf(133)
        "first_weapon" -> listOf(101, 108, 110, 111, 113, 114, 115, 117, 223, 253, 254)
        "second_weapon" -> listOf(112, 189)
        "emblem" -> listOf(537, 646)
        else -> listOf()
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
            39 -> {
                result = "${floor(firstParam(params, level).toDouble()).toInt()}% de armadura recibida"
            }
            2001 -> {
                var job = jobs.find { j -> j[Jobs.id] == params[2].toInt() }
                result = "${floor(firstParam(params, level).toDouble()).toInt()}% cantidad de recolección en ${job?.get(Jobs.name_es)}"
            }
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

fun sha256(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(value.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) } // Convierte cada byte en un string hexadecimal
}

fun validateSha256(foundPassword: String, enteredPassword: String): Boolean {
    val calculatedHash = sha256(enteredPassword)
    return calculatedHash.equals(foundPassword, ignoreCase = true)
}

fun checkUsername(user: String, ar: AccountsRepository): Boolean {
    val account = ar.getAccountByName(user)
    println("checkUsername: $account")

    return account != null
}

fun checkAccount(user: String, password: String, ar: AccountsRepository): Boolean {
    val account = ar.getAccountByName(user)

    if (account != null) {
        if (validateSha256(account[Accounts.password], password)) return true
    }

    return false
}

/**
 * Crea un código para la build que no exista en otra build
 */
fun generarCodigo(br: BuildsRepository): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    var code = (1..5).map { chars.random() }.joinToString("")
    while (checkBuildCode(code, br)) {
        code = (1..5).map { chars.random() }.joinToString("")
    }

    return code
}

/**
 * Comprueba si ya existe una build con ese código. Devuelve TRUE en caso de que sí, FALSE en caso de no existir
 */
fun checkBuildCode(code: String, br: BuildsRepository): Boolean {

    return br.getBuildByCode(code) != null
}

fun getSpriteHolderByName(name: String): String {
    return when (name) {
        "helmet" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/HEAD.png"
        "neck" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/NECK.png"
        "chest" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/CHEST.png"
        "left_ring" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEFT_HAND.png"
        "right_ring" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/RIGHT_HAND.png"
        "boots" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/LEGS.png"
        "cape" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BACK.png"
        "epaulettes" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SHOULDERS.png"
        "belt" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/BELT.png"
        "mount" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/MOUNT.png"
        "pet" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/PET.png"
        "emblem" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/ACCESSORY.png"
        "first_weapon" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/FIRST_WEAPON.png"
        "second_weapon" -> "https://tmktahu.github.io/WakfuAssets/equipmentDefaults/SECOND_WEAPON.png"

        else -> {
            return ""
        }
    }
}
