package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.output.CommandLineWriter;
import com.booster.service.LanguageBeingLearnedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedCommandHandler implements CommandHandler {

    private final LanguageBeingLearnedService languageBeingLearnedService;

    private final CommandLineWriter commandLineWriter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (AddLanguageBeingLearnedArgs) commandWithArguments.getArgs();
            languageBeingLearnedService.addWithDefaultVocabulary(args.getLanguageId());
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
        commandLineWriter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE_BEING_LEARNED;
    }

}
