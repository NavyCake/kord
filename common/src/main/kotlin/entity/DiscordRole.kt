package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordRole(
    val id: Snowflake,
    val name: String,
    val color: Int,
    val hoist: Boolean,
    val position: Int,
    val permissions: Permissions,
    val managed: Boolean,
    val mentionable: Boolean,
    val tags: Optional<DiscordRoleTags> = Optional.Missing(),
    val icon: String?
)

@Serializable
data class DiscordRoleTags(
    val botId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("integration_id")
    val integrationId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("premium_subscriber")
    val premiumSubscriber: Optional<DiscordNull?> = Optional.Missing(),
)

@Serializable
data class DiscordPartialRole(
    val id: Snowflake,
    val name: Optional<String> = Optional.Missing(),
    val color: OptionalInt = OptionalInt.Missing,
    val hoist: OptionalBoolean = OptionalBoolean.Missing,
    val position: OptionalInt = OptionalInt.Missing,
    val permissions: Optional<Permissions> = Optional.Missing(),
    val managed: OptionalBoolean = OptionalBoolean.Missing,
    val mentionable: OptionalBoolean = OptionalBoolean.Missing,
    val tags: Optional<DiscordRoleTags> = Optional.Missing(),
    val icon: Optional<String> = Optional.Missing()
)

@Serializable
data class DiscordAuditLogRoleChange(
    val id: String,
    val name: String? = null,
    val color: Int? = null,
    val hoist: Boolean? = null,
    val position: Int? = null,
    val permissions: Permissions? = null,
    val managed: Boolean? = null,
    val mentionable: Boolean? = null,
    val icon: String?
)

@Serializable
data class DiscordGuildRole(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val role: DiscordRole,
)

@Serializable
data class DiscordDeletedGuildRole(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("role_id")
    val id: Snowflake,
)