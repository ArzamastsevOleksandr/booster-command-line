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

    // todo: can add ENGLISH many times (sql fix)
    public void handle(CommandWithArguments commandWithArguments) {
        if (noErrors(commandWithArguments)) {
            var args = (AddLanguageBeingLearnedArgs) commandWithArguments.getArgs();
            languageBeingLearnedDao.add(args.getLanguageId());
            commandLineWriter.writeLine("Done.");
            commandLineWriter.newLine();
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

}
