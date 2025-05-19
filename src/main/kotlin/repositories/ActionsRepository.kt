package repositories

import Actions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

        transaction {
            SchemaUtils.create(Actions)

            result = Actions.select(Actions.id eq id).singleOrNull()
        }

        return result
    }

    fun getAllActions(): List<ResultRow> {
        Database.connect(dbUrl, driver, dbUsername, dbPassword)

        var result: List<ResultRow> = emptyList()

        transaction {
            SchemaUtils.create(Actions)

            result = Actions.selectAll().toList()
        }

        return result
    }
}