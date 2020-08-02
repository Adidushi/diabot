package com.dongtronic.diabot.data.mongodb

import com.dongtronic.diabot.platforms.discord.commands.nightscout.NightscoutSetDisplayCommand
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.dv8tion.jda.api.entities.User

@JsonAutoDetect
data class NightscoutUserDTO(
        val userId: Long = -1,
        val url: String? = null,
        val token: String? = null,
        val displayOptions: List<String> = NightscoutSetDisplayCommand.enabledOptions.toList(),
        val publicGuilds: List<Long> = emptyList(),
        @JsonIgnore
        val jdaUser: User? = null
) {
    /**
     * Nightscout's API endpoint
     */
    val apiEndpoint: String
        get() {
            val trimmed = url!!.removeSuffix("/")
            return trimmed.plus("/api/v1")
        }

    /**
     * Checks if this NS user has set their NS to be public in the provided guild.
     *
     * @param guildId The ID of the guild
     * @return Whether this Nightscout is public in this guild.
     */
    fun isNightscoutPublic(guildId: Long): Boolean {
        return publicGuilds.contains(guildId) || userId == -1L
    }
}