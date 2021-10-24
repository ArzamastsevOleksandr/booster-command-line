package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageDao;
import com.booster.model.Language;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler implements CommandHandler {

    private final LanguageDao languageDao;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            List<Language> languages = languageDao.findAll();

            if (languages.isEmpty()) {
                commandLineWriter.writeLine("There are no languages in the system now.");
            } else {
                commandLineWriter.writeLine("All languages:");
                commandLineWriter.newLine();

                for (var language : languages) {
                    commandLineWriter.writeLine(language.toString());
                }
            }
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_LANGUAGES;
    }

}
