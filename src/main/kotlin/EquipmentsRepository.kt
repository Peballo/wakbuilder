import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

        try {
            transaction {
                SchemaUtils.create(Equipments)
                result = Equipments.selectAll().where { Equipments.item_type eq type }.toList()
            }
        } catch (nsee: NoSuchElementException) {
            result = listOf()
        }

        return result
    }

    fun getEquipmentsByTypeAndLevel(type: Int, level: Int): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: List<ResultRow> = listOf()

        try {
            transaction {
                SchemaUtils.create(Equipments)
                result = Equipments.selectAll().andWhere {Equipments.item_type eq type}.andWhere {Equipments.level eq level}.toList()
            }
        } catch (nsee: NoSuchElementException) {
            result = listOf()
        }

        return result
    }
}