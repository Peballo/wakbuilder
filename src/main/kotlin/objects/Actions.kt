import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Actions : Table("actions") {
    val id: Column<Int> = integer("id")
    val desc_es: Column<String> = varchar("desc_es", 3000)
    val desc_en: Column<String> = varchar("desc_en", 3000)
    val desc_fr: Column<String> = varchar("desc_fr", 3000)
    val desc_pt: Column<String> = varchar("desc_pt", 3000)

    override val primaryKey = PrimaryKey(id)
}