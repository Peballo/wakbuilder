package repositories

import objects.Accounts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class AccountsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    fun getAccountById(id: Int): ResultRow? {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)
        var result: ResultRow? = null

        transaction {
            SchemaUtils.create(Accounts)

            result = Accounts.select(Accounts.id eq id).singleOrNull()
        }

        return result
    }
}