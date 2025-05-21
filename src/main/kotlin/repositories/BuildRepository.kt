package repositories

import components.BuildItemsList
import components.CharacterStats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BuildsRepository (
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

    fun getAllBuilds(): List<ResultRow> {
        return transaction {
            Builds.selectAll().toList()
        }
    }

    fun getAllBuildsByUserId(id: Int): List<ResultRow> {
        return transaction {
            Builds.selectAll().where {Builds.user eq id}.toList()
        }
    }

    fun insertBuild(buildCode: String, buildName: String, buildLevel: Int, buildCharacter: String, buildUser: Int) {
        transaction {
            Builds.insert {
                it[Builds.id] = buildCode
                it[Builds.name] = buildName
                it[Builds.level] = buildLevel
                it[Builds.character] = buildCharacter
                it[Builds.user] = buildUser
                it[Builds.helmet] = -1
                it[Builds.neck] = -1
                it[Builds.chest] = -1
                it[Builds.left_ring] = -1
                it[Builds.right_ring] = -1
                it[Builds.boots] = -1
                it[Builds.cape] = -1
                it[Builds.epaulettes] = -1
                it[Builds.belt] = -1
                it[Builds.mount] = -1
                it[Builds.pet] = -1
                it[Builds.emblem] = -1
                it[Builds.first_weapon] = -1
                it[Builds.second_weapon] = -1
            }
        }
    }

    fun updateBuild(stats: CharacterStats, build: BuildItemsList) {
        println("UpdateBuild ${stats.code}")
        println(build)
        transaction {
            Builds.update({ Builds.id eq stats.code}) {
                it[Builds.name] = stats.name
                it[Builds.level] = stats.level
                if (stats.account > -1) it[Builds.user] = stats.account
                if (build.helmet != null) it[Builds.helmet] = build.helmet!![Equipments.id]
                if (build.neck != null) it[Builds.neck] = build.neck!![Equipments.id]
                if (build.chest != null) it[Builds.chest] = build.chest!![Equipments.id]
                if (build.left_ring != null)  it[Builds.left_ring] = build.left_ring!![Equipments.id]
                if (build.right_ring != null) it[Builds.right_ring] = build.right_ring!![Equipments.id]
                if (build.boots != null) it[Builds.boots] = build.boots!![Equipments.id]
                if (build.cape != null) it[Builds.cape] = build.cape!![Equipments.id]
                if (build.epaulettes != null) it[Builds.epaulettes] = build.epaulettes!![Equipments.id]
                if (build.belt != null) it[Builds.belt] = build.belt!![Equipments.id]
                if (build.mount != null) it[Builds.mount] = build.mount!![Equipments.id]
                if (build.pet != null) it[Builds.pet] = build.pet!![Equipments.id]
                if (build.emblem != null) it[Builds.emblem] = build.emblem!![Equipments.id]
                if (build.first_weapon != null) it[Builds.first_weapon] = build.first_weapon!![Equipments.id]
                if (build.second_weapon != null) it[Builds.second_weapon] = build.second_weapon!![Equipments.id]
                it[Builds.character] = stats.character
            }
        }
    }
}