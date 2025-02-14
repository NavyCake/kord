package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import io.ktor.http.*
import java.util.*

/**
 * The behavior of a [Discord User](https://discord.com/developers/docs/resources/user)
 */
interface UserBehavior : KordEntity, Strategizable {

    val mention: String get() = "<@${id.asString}>"

    /**
     * Requests to get the this behavior as a [Member] in the [Guild] with the [guildId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    suspend fun asMember(guildId: Snowflake): Member = supplier.getMember(guildId = guildId, userId = id)

    /**
     * Requests to get this behavior as a [Member] in the [Guild] with the [guildId],
     * returns null if the member isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asMemberOrNull(guildId: Snowflake): Member? = supplier.getMemberOrNull(guildId = guildId, userId = id)


    /**
     * Requests to get the this behavior as a [User].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    suspend fun asUser(): User = supplier.getUser(id)

    /**
     * Requests to get this behavior as a [User],
     * returns null if the user isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asUserOrNull(): User? = supplier.getUserOrNull(id)


    /**
     * Requests to get or create a [DmChannel] between this bot and the user.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * This method will throw a [RestRequestException] if a user does not allow you to send DM's to them,
     * use [getDmChannelOrNull] instead to retrieve the [DmChannel] safely.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getDmChannel(): DmChannel {
        val response = kord.rest.user.createDM(DMCreateRequest(id))
        val data = ChannelData.from(response)

        return Channel.from(data, kord) as DmChannel
    }

    /**
     * Requests to get or create a [DmChannel] between this bot and the user, returns null if the user does not allow DMs.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getDmChannelOrNull(): DmChannel? {
        return try {
            getDmChannel()
        } catch (exception: RestRequestException) {
            val code = exception.error?.code
            when {
                code == JsonErrorCode.CannotSendMessagesToUser -> null
                code == null && exception.status.code == HttpStatusCode.Forbidden.value -> null
                else -> throw exception
            }
        }
    }

    /**
     * returns a new [UserBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UserBehavior = UserBehavior(id, kord, strategy)
}

fun UserBehavior(
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): UserBehavior = object : UserBehavior {
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is UserBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "UserBehavior(id=$id, kord=kord, supplier=$supplier)"
    }
}
