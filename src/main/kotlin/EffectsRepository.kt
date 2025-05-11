import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class EffectsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getEffectById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: ResultRow? = null

        try {
            transaction {
                SchemaUtils.create(Effects)
                result = Effects.selectAll().where { Effects.id eq id }.first()
            }
        } catch (nsee: NoSuchElementException) {
            result = null
        }

        return result
    }

    fun getAllEffects(): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: List<ResultRow> = emptyList()

        try {
            transaction {
                SchemaUtils.create(Effects)
                result = Effects.selectAll().toList()
            }
        } catch (nsee: NoSuchElementException) {
            result = emptyList()
        }

        return result
    }
}