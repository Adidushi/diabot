package com.dongtronic.diabot.commands.admin

import com.dongtronic.diabot.commands.DiabotCommand
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.core.Permission
import org.slf4j.LoggerFactory

/**
 * @author John Grosh (jagrosh)
 */
class ShutdownCommand(category: Command.Category) : DiabotCommand(category, null) {

    init {
        this.name = "shutdown"
        this.help = "safely shuts off the bot"
        this.guildOnly = false
        this.ownerCommand = false
        this.aliases = arrayOf("heckoff", "fuckoff", "removethyself", "remove")
        this.userPermissions = arrayOf(Permission.ADMINISTRATOR)
        this.hidden = true
    }

    override fun execute(event: CommandEvent) {
        val userId = event.author.id
        var allowed = false

        //TODO: replace list of allowed admins with config file
        val allowedUsers = arrayOf(
                "125616270254014464", //Adi
                "189436077793083392"  //Cas
        )


        for (user in allowedUsers) {
            if (user == userId) {
                allowed = true
                break
            }
        }

        if (allowed) {
            logger.info("Shutting down bot (requested by " + event.author.name + " - " + userId + ")")
            event.replyWarning("Shutting down (requested by " + event.author.name + ")")
            event.reactWarning()
            event.jda.shutdown()
        }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(ShutdownCommand::class.java)
    }

}