import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class StatesRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getStateById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: ResultRow? = null

        try {
            transaction {
                SchemaUtils.create(States)

                result = States.selectAll().where { States.id eq id }.first()
            }
        } catch (nsee: NoSuchElementException) {
            result = null
        }

        return result
    }
}