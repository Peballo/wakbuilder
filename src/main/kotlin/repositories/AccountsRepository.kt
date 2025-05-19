package repositories

import objects.Accounts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AccountsRepository (
    private val dbUrl: String,
    private val driver: String,
    private val dbUsername: String,
    private val dbPassword: String
) {
    init {
        Database.connect (
            url = dbUrl,
            driver = driver,
            user = dbUsername,
            password = dbPassword
        )
    }

    fun getAccountByName(username: String): ResultRow? {
        return transaction {
            Accounts.selectAll().where {Accounts.username eq username}.firstOrNull()
        }
    }

    fun insertAccount(name: String, pass: String): Int {
        return transaction {
            SchemaUtils.create(Accounts)

            val id = Accounts.insert {
                it[username] = name
                it[password] = pass
            } get Accounts.id

            commit()
            id.value
        }
    }
}