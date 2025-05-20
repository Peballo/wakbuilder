package repositories

import Equipments
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction

class EquipmentsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getAllEquipments() {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: List<ResultRow> = listOf()

        try {
            transaction {
                SchemaUtils.create(Equipments)
                result = Equipments.selectAll().toList()
            }
        } catch (nsee: NoSuchElementException) {
            result = listOf()
        }
    }

    fun getEquipmentById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: ResultRow? = null

        try {
            transaction {
                SchemaUtils.create(Equipments)

                result = Equipments.selectAll().where { Equipments.id eq id }.first()
            }
        } catch (nsee: NoSuchElementException) {
            result = null
        }

        return result
    }

    fun getEquipmentsByType(type: Int): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: List<ResultRow> = listOf()


        transaction {
            SchemaUtils.create(Equipments)
            result = Equipments.selectAll().where { Equipments.item_type eq type }.toList()
        }


        return result
    }

    fun getEquipmentsByTypeAndLevel(type: Int, level: Int): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: List<ResultRow> = listOf()


        transaction {
            SchemaUtils.create(Equipments)
            result = Equipments.selectAll().andWhere { Equipments.item_type eq type}.andWhere { Equipments.level eq level}.toList()
        }


        return result
    }

    fun getEquipmentsByTypesAndLevel(types: List<Int>, level: Int): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: List<ResultRow> = listOf()


        transaction {
            SchemaUtils.create(Equipments)
            result = Equipments.selectAll().andWhere { Equipments.item_type inList types}.andWhere { Equipments.level eq level }.toList()
        }


        return result
    }

    fun getEquipmentsByTypeAndLevelRange(type: Int, minLevel: Int, maxLevel: Int): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: List<ResultRow> = listOf()

        transaction {
            SchemaUtils.create(Equipments)
            result = Equipments.selectAll()
                .andWhere { Equipments.item_type eq type }
                .andWhere { Equipments.level greaterEq minLevel }
                .andWhere { Equipments.level lessEq maxLevel }
                .toList()
        }

        return result
    }


    fun getEquipmentsByTypeAndMultipleLevelRange(
        type: Int,
        selectedRanges: Set<String>,
        selectedRarities: Set<Int> = setOf()
    ): List<ResultRow> {
        return getEquipmentsByTypeAndMultipleLevelRange(listOf(type), selectedRanges, selectedRarities)
    }

    fun getEquipmentsByTypeAndMultipleLevelRange(
        types: List<Int>,
        selectedRanges: Set<String>,
        selectedRarities: Set<Int> = setOf()
    ): List<ResultRow> {

        // Primero, limpiamos los rangos vacíos
        val cleanRanges = selectedRanges.filter { it.isNotEmpty() }.toSet()

        // Si no hay rangos después de limpiar, retornar todos los items del tipo especificado
        if (cleanRanges.isEmpty()) {
            return transaction {
                var query = Equipments.selectAll()
                    .andWhere { Equipments.item_type inList types }

                if (selectedRarities.isNotEmpty()) {
                    query = query.andWhere { Equipments.rarity inList selectedRarities }
                }

                query.orderBy(Equipments.id to SortOrder.DESC).toList()
            }
        }

        // Procesar los rangos de nivel
        val levelRanges = cleanRanges.mapNotNull { range ->
            try {
                val (min, max) = range.split("-")
                    .map { it.trim().toInt() }
                if (min <= max) min to max else null
            } catch (e: Exception) {
                println("Debug - Error processing range: $range")
                null
            }
        }

        return transaction {
            var query = Equipments.selectAll()
                .andWhere { Equipments.item_type inList types }

            // Filtrar por nivel
            if (levelRanges.isNotEmpty()) {
                val levelConditions = levelRanges.map { (min, max) ->
                    (Equipments.level greaterEq min) and (Equipments.level lessEq max)
                }
                query = query.andWhere { levelConditions.reduce { acc, condition -> acc or condition } }
            }

            // Filtrar por rareza
            if (selectedRarities.isNotEmpty()) {
                query = query.andWhere { Equipments.rarity inList selectedRarities }
            }

            query.orderBy(Equipments.id to SortOrder.DESC).toList()
        }
    }
}