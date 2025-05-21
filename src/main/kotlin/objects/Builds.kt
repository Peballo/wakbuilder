import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Builds: Table("builds") {
    val id: Column<String> = text("id")
    val name: Column<String> = text("name")
    val level: Column<Int> = integer("level")
    val user: Column<Int> = integer("user")
    val helmet: Column<Int> = integer("helmet")
    val neck: Column<Int> = integer("neck")
    val chest: Column<Int> = integer("chest")
    val left_ring: Column<Int> = integer("left_ring")
    val right_ring: Column<Int> = integer("right_ring")
    val boots: Column<Int> = integer("boots")
    val cape: Column<Int> = integer("cape")
    val epaulettes: Column<Int> = integer("epaulettes")
    val belt: Column<Int> = integer("belt")
    val mount: Column<Int> = integer("mount")
    val pet: Column<Int> = integer("pet")
    val emblem: Column<Int> = integer("emblem")
    val first_weapon: Column<Int> = integer("first_weapon")
    val second_weapon: Column<Int> = integer("second_weapon")
    val character: Column<String> = text("class")

    override val primaryKey = PrimaryKey(id)
}