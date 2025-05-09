import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Effects : Table("effects") {
    val id: Column<Int> = integer("id")
    val action: Column<Int> = integer("action")
    val params: Column<List<Int>> = array<Int>("params")

    override val primaryKey = PrimaryKey(id)
}