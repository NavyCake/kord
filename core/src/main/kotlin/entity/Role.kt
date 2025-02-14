package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.cache.data.RoleData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image
import java.util.*

data class Role(
    val data: RoleData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : RoleBehavior {

    override val id: Snowflake
        get() = data.id

    override val guildId: Snowflake
        get() = data.guildId

    val color: Color get() = Color(data.color)

    val hoisted: Boolean get() = data.hoisted

    val managed: Boolean get() = data.managed

    val mentionable: Boolean get() = data.mentionable

    val name: String get() = data.name
    
    val icon: RoleIcon get() = RoleIcon(data, kord)

    val permissions: Permissions get() = data.permissions

    val rawPosition: Int get() = data.position

    /**
     * The tags of this role, if present.
     */
    val tags: RoleTags? get() = data.tags.unwrap { RoleTags(it, guildId, kord) }

    override fun compareTo(other: Entity): Int = when (other) {
        is Role -> compareBy<Role> { it.rawPosition }.thenBy { it.guildId }.compare(this, other)
        else -> super.compareTo(other)
    }

    /**
     * Returns a new [Role] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Role = Role(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is RoleBehavior -> other.id == id && other.guildId == guildId
        else -> false
    }

    override fun toString(): String {
        return "Role(data=$data, kord=$kord, supplier=$supplier)"
    }

    data class RoleIcon(val data: RoleData, override val kord: Kord) : KordObject {
        /**
         * Gets the avatar url in given [String], or returns null if the [NullPointerException]
         */
        val getIconUrl: String? get() = if(data.icon == null) null else "https://cdn.discordapp.com/role-icons/${data.id}/${data.id}.png"

        /**
         * Gets the avatar url in given [String], or returns null if the [NullPointerException]
         */
        fun getUrl(size: Image.Size): String? {
            return if(data.icon == null) null else "https://cdn.discordapp.com/role-icons/${data.id}/${data.icon}.png?size=${size.maxRes}"
        }

        /**
         * Gets the avatar url in given [String], or returns null if the [NullPointerException]
         */
        fun getUrl(format: Image.Format, size: Image.Size): String? {
            return if(data.icon == null) null else "https://cdn.discordapp.com/role-icons/${data.id}/${data.icon}.${format.extension}?size=${size.maxRes}"
        }
    }

}
