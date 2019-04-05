package com.dongtronic.diabot.listener

import com.dongtronic.diabot.util.Patterns
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import java.util.*

class OhNoListener : ListenerAdapter() {
    private val logger = LoggerFactory.getLogger(OhNoListener::class.java)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        val channel = event.channel

        val message = event.message.contentRaw

        val matcher = Patterns.ohNoPattern.matcher(message)

        if (!matcher.matches()) {
            return
        }

        val r = Random()

        val number = r.nextInt(100)

        if(number < 50) {
            return
        }

        channel.sendMessage("oh no").queue()


    }

}
