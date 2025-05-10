import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ActionsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getActionById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: ResultRow? = null

        try {
            transaction {
                SchemaUtils.create(Actions)

                result = Actions.selectAll().where { Actions.id eq id }.first()
            }
        } catch (nsee: NoSuchElementException) {
            result = null
        }

        return result
    }
}