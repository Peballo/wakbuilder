import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class JobsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getJobById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: ResultRow? = null

        try {
            transaction {
                SchemaUtils.create(Jobs)
                result = Jobs.selectAll().where { Jobs.id eq id }.first()
            }
        } catch (nsee: NoSuchElementException) {
            result = null
        }

        return result
    }

    fun getAllJobs(): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: List<ResultRow> = emptyList()

        try {
            transaction {
                SchemaUtils.create(Jobs)
                result = Jobs.selectAll().toList()
            }
        } catch (nsee: NoSuchElementException) {
            result = emptyList()
        }

        return result
    }
}