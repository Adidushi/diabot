package com.dongtronic.diabot.platforms.discord.utils

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.User

object NicknameUtils {

    fun determineAuthorDisplayName(event: CommandEvent): String {
        return determineDisplayName(event, event.author)
    }

    fun determineDisplayName(event: CommandEvent, user: User): String {
        return event.guild.getMember(user)!!.effectiveName
    }
}
