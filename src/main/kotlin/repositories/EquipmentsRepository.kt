package repositories

import Equipments
import org.jetbrains.exposed.sql.*
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
        selectedRanges: Set<String>
    ): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: List<ResultRow> = listOf()

        // Convertir los rangos de String a pares de números
        val ranges = selectedRanges.map { rangeStr ->
            val levels = rangeStr.split("-").map { it.trim().toInt() }
            Pair(levels[0], levels[1])
        }

        transaction {
            SchemaUtils.create(Equipments)
            result = Equipments.selectAll()
                .andWhere { Equipments.item_type eq type }
                .andWhere {
                    ranges.map { (min, max) ->
                        (Equipments.level greaterEq min) and (Equipments.level lessEq max)
                    }.reduce { acc, op -> acc or op }
                }
                .orderBy(Equipments.id to SortOrder.ASC).toList()
        }

        return result
    }
}