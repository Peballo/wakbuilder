import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Jobs : Table("jobs") {
    val id: Column<Int> = integer("id")
    val name_es: Column<String> = varchar("name_es", 255)
    val name_en: Column<String> = varchar("name_en", 255)
    val name_fr: Column<String> = varchar("name_fr", 255)
    val name_pt: Column<String> = varchar("name_pt", 255)

    override val primaryKey = PrimaryKey(id)
}