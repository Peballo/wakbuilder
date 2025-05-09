import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Equipments : Table("equipments") {
    val id: Column<Int> = integer("id")
    val level: Column<Int> = integer("level")
    val rarity: Column<Int> = integer("rarity")
    val sprite_id: Column<Int> = integer("sprite")
    val item_type: Column<Int> = integer("type")
    val name_es: Column<String> = varchar("name_es", 255)
    val name_en: Column<String> = varchar("name_en", 255)
    val name_fr: Column<String> = varchar("name_fr", 255)
    val name_pt: Column<String> = varchar("name_pt", 255)
    val desc_es: Column<String> = varchar("desc_es", 3000)
    val desc_en: Column<String> = varchar("desc_en", 3000)
    val desc_fr: Column<String> = varchar("desc_fr", 3000)
    val desc_pt: Column<String> = varchar("desc_pt", 3000)
    val effects: Column<List<Int>> = array<Int>("effects")

    override val primaryKey = PrimaryKey(id)
}