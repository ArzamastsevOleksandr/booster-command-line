package com.booster.command.handler;

import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedCommandHandler implements CommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (AddLanguageBeingLearnedArgs) commandWithArguments.getArgs();
            languageBeingLearnedDao.add(args.getLanguageId());
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
        commandLineWriter.newLine();
    }

}
