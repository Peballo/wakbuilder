package objects

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Accounts : IntIdTable("accounts") {
    val username: Column<String> = text("username")
    val password: Column<String> = text("password")
}