package com.dongtronic.diabot.commands;

import com.dongtronic.diabot.converters.A1cConverter;
import com.dongtronic.diabot.data.A1cDTO;
import com.dongtronic.diabot.exceptions.UnknownUnitException;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dongtronic.diabot.converters.GlucoseUnit.MGDL;
import static com.dongtronic.diabot.converters.GlucoseUnit.MMOL;

@CommandInfo(
    name = {"estimate"},
    description = "estimate A1c from average blood glucose, or average blood glucose from A1c"
)

public class EstimationCommand extends DiabotCommand {

  private Logger logger = LoggerFactory.getLogger(EstimationCommand.class);

  public EstimationCommand(Category category) {
    this.name = "estimate";
    this.help = "estimate A1c from average blood glucose, or average blood glucose from A1c";
    this.guildOnly = false;
    this.arguments = "<a1c/average> <number> [unit]";
    this.category = category;
    this.examples = new String[]{"diabot estimate a1c 120", "diabot estimate a1c 5.7", "diabot estimate a1c 120 mg/dL", "diabot estimate average 6.7", "diabot estimate average 42"};
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.replyWarning("You didn't give me a value!");
    } else {
      // split the arguments on all whitespaces
      String[] items = event.getArgs().split("\\s+");

      if(items.length < 2) {
        event.replyWarning("Required arguments: `mode` & `value`\nexample: diabot estimate a1c 6.9");
      }

      switch (items[0].toUpperCase()) {
        case "A1C":
          estimateA1c(event);
          break;
        case "AVERAGE":
          estimateAverage(event);
          break;
        default:
          event.replyError("Unknown mode. Choose either `a1c` or `average`");
          break;
      }


    }
  }

  private void estimateAverage(CommandEvent event) {
    String[] items = event.getArgs().split("\\s+");
    String pattern = "[^0-9\\.]";
    String number = items[1].replaceAll(pattern, "");

    A1cDTO result;

    result = A1cConverter.estimateAverage(number);

    event.reply(String.format("An A1c of **%s%%** (DCCT) or **%s mmol/mol** (IFCC) is about **%s mg/dL** or **%s mmol/L**", result.getDcct(), result.getIfcc(), result.getOriginal().getMgdl(), result.getOriginal().getMmol()));

  }

  private void estimateA1c(CommandEvent event) {
    // split the arguments on all whitespaces
    String[] items = event.getArgs().split("\\s+");

    A1cDTO result;

    try {

      logger.info("Estimating A1c for average " + items[0]);

      if(items.length == 3) {
        result = A1cConverter.estimateA1c(items[1], items[2]);
      } else {
        result = A1cConverter.estimateA1c(items[1], null);
      }

      if (result.getOriginal().getInputUnit() == MMOL) {
        event.reply(String.format("An average of %s mmol/L is about **%s%%** (DCCT) or **%s mmol/mol** (IFCC)", result.getOriginal().getMmol(), result.getDcct(), result.getIfcc()));
      } else if (result.getOriginal().getInputUnit() == MGDL) {
        event.reply(String.format("An average of %s mg/dL is about **%s%%** (DCCT) or **%s mmol/mol** (IFCC)", result.getOriginal().getMgdl(), result.getDcct(), result.getIfcc()));
      } else {
        //TODO: Make arguments for result.getDcct and result.getIfcc less confusing. ie: not wrong
        String reply =
            String.format("An average of %s mmol/L is about **%s%%** (DCCT) or **%s mmol/mol** (IFCC) %n", result.getOriginal().getOriginal(), result.getDcct(MGDL), result.getIfcc(MGDL)) +
                String.format("An average of %s mg/dL is about **%s%%** (DCCT) or **%s mmol/mol** (IFCC)", result.getOriginal().getOriginal(), result.getDcct(MMOL), result.getIfcc(MMOL));
        event.reply(reply);
      }

    } catch (IllegalArgumentException ex) {
      // Ignored on purpose
      logger.warn("IllegalArgumentException occurred but was ignored in BG conversion");
    } catch (UnknownUnitException ex) {
      event.replyError("I don't know how to convert from " + items[1]);
      logger.warn("Unknown BG unit " + items[1]);
    }
  }
}
