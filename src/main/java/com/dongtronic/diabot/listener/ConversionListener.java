package com.dongtronic.diabot.listener;

import com.dongtronic.diabot.converters.BloodGlucoseConverter;
import com.dongtronic.diabot.converters.GlucoseUnit;
import com.dongtronic.diabot.data.ConversionDTO;
import com.dongtronic.diabot.exceptions.UnknownUnitException;
import com.dongtronic.diabot.util.Patterns;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionListener extends ListenerAdapter {
  private Logger logger = LoggerFactory.getLogger(ConversionListener.class);

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) return;

    TextChannel channel = event.getChannel();

    Message message = event.getMessage();
    String messageText = message.getContentRaw();

    Matcher inlineMatcher = Patterns.inlineBgPattern.matcher(messageText);
    Matcher separateMatcher = Patterns.separateBgPattern.matcher(messageText);
    Matcher unitMatcher = Patterns.unitBgPattern.matcher(messageText);

    String number = "";
    String unit = "";

    if(unitMatcher.matches()) {
      number = unitMatcher.group(4);
      unit = unitMatcher.group(5);
    }

    if(inlineMatcher.matches()) {
      number = inlineMatcher.group(1);
    } else if (separateMatcher.matches()) {
      number = separateMatcher.group(1);
    }

    if(number.length() == 0) {
      return;
    }

    try {
      ConversionDTO result;
      if(unit.length() > 1) {
        result = BloodGlucoseConverter.convert(number, unit);
      } else {
        result = BloodGlucoseConverter.convert(number, null);
      }

      if (result.getInputUnit() == GlucoseUnit.MMOL) {
        channel.sendMessage(String.format("%s mmol/L is %s mg/dL", result.getOriginal(), result.getConverted())).queue();
      } else if (result.getInputUnit() == GlucoseUnit.MGDL) {
        channel.sendMessage(String.format("%s mg/dL is %s mmol/L", result.getOriginal(), result.getConverted())).queue();
      } else {
        String reply = String.join(
            "%n",
            "*I'm not sure if you gave me mmol/L or mg/dL, so I'll give you both.*",
            "%s mg/dL is **%s mmol/L**",
            "%s mmol/L is **%s mg/dL**");

        channel.sendMessage(String.format(reply, result.getOriginal(), result.getMmol(), result.getOriginal(),
            result.getMgdl())).queue();
      }

    } catch (IllegalArgumentException ex) {
      // Ignored on purpose
      logger.warn("IllegalArgumentException occurred but was ignored in BG conversion");
    } catch (UnknownUnitException ex) {
      // Ignored on purpose
    }

  }

}
