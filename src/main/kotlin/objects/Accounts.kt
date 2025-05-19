package objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Accounts : Table("accounts") {
    val id: Column<Int> = integer("id").autoIncrement()
    val username: Column<String> = text("username")
    val password: Column<String> = text("password")
}