package dev.kord.core.exception

import dev.kord.common.entity.Snowflake

class GatewayNotFoundException : Exception {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    companion object {
        inline fun voiceConnectionGatewayNotFound(guildId: Snowflake): Nothing =
            throw GatewayNotFoundException("Wasn't able to find a gateway for the guild with id $guildId while creating a VoiceConnection!")
    }
}
