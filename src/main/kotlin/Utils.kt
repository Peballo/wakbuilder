import org.jetbrains.exposed.sql.ResultRow

data class Condition (val name: String, val value: Regex)

fun firstParam(params: List<Int>, level: Int): Int {
    return params[0] + params[1] * level
}

fun secondParam(params: List<Int>, level: Int): Int {
    return params[2] + params[3] * level
}

fun thirdParam(params: List<Int>, level: Int): Int {
    return params[4] + params[5] * level
}

// [>2]
fun isLastStackValueGreaterThanTwo(stack: Int): Boolean {
    return stack >= 2
}

// [>2]?s:
fun plural(stack: Int): String {
    return if (isLastStackValueGreaterThanTwo(stack)) "s" else ""
}

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

fun parseEffect(effect: ResultRow, level: Int): String {
    val ar: ActionsRepository = ActionsRepository("jdbc:postgresql://localhost:5432/wakbuilder", "org.postgresql.Driver", "postgres", "1234")
    var result = "Not found"

    val action: ResultRow? = ar.getActionById(effect[Effects.action])

    if (action != null) {
        result = action[Actions.desc_es]

        when (action[Actions.id]) {
            304 -> println("Estado")
            39 -> println("Algo raro")
            2001 -> println("Profesión")
            else -> {
                var desc: String = action[Actions.desc_es]
                val params: List<Int> = effect[Effects.params]
                var stack: Int = 0
                val hasThreeOrMoreArguments: Boolean = params.size >= 6 // [~3]
                var computedParamNotFound: Boolean = true

                while (computedParamNotFound) {
                    computedParamNotFound = false

                    var firstCondition: String = detectFirstCondition(desc)

                    if (firstCondition.length > 0) {
                        computedParamNotFound = true

                        if (firstCondition.equals("{[~3]?")) {
                            if (!hasThreeOrMoreArguments) {
                                desc = desc.substring(desc.indexOf(":")+1, desc.length-1)
                            }
                        } else if (firstCondition == "{[~2]?") {
                            desc = desc.replace(firstCondition, " ")
                            desc = desc.replace(":}", "")
                        } else if (firstCondition == "{[>2]?s:}") {
                            desc = desc.replace(firstCondition, plural(stack))
                        } else if (firstCondition == "[#1]") {
                            desc = desc.replace(firstCondition, "${Math.floor(firstParam(params, level).toDouble()).toInt()}")
                        } else if (firstCondition == "[#2]") {
                            if (action[Actions.id] == 2001) desc = desc.replace(firstCondition, "${params[2]}")
                            else desc = desc.replace(firstCondition, "${Math.floor(secondParam(params, level).toDouble()).toInt()}")
                        } else if (firstCondition == "[#3]") {
                            desc = desc.replace(firstCondition, "${Math.floor(thirdParam(params, level).toDouble()).toInt()}")
                        }
                    }
                }

                desc = desc.replace("[el1]", "Fuego")
                desc = desc.replace("[el2]", "Agua")
                desc = desc.replace("[el3]", "Tierra")
                desc = desc.replace("[el4]", "Aire")

                result = desc
            }
        }
    }

    return result
}