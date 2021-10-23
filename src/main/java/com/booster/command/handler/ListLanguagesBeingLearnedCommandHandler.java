package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListLanguagesBeingLearnedArgs;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import com.booster.output.CommandLineWriter;
import com.booster.service.LanguageBeingLearnedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesBeingLearnedCommandHandler implements CommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final LanguageBeingLearnedService languageBeingLearnedService;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ListLanguagesBeingLearnedArgs) commandWithArguments.getArgs();

            args.getId().ifPresentOrElse(
                    this::displayLanguageBeingLearnedById,
                    this::displayAllLanguagesBeingLearned
            );
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    private void displayLanguageBeingLearnedById(Long id) {
        commandLineWriter.writeLine(languageBeingLearnedService.findById(id).get().toString());
    }

    private void displayAllLanguagesBeingLearned() {
        List<LanguageBeingLearned> languagesBeingLearned = languageBeingLearnedDao.findAll();

        if (languagesBeingLearned.isEmpty()) {
            commandLineWriter.writeLine("You are not learning any languages yet.");
        } else {
            commandLineWriter.writeLine("All languages being learned:");
            commandLineWriter.newLine();

            for (var languageBeingLearned : languagesBeingLearned) {
                commandLineWriter.writeLine(languageBeingLearned.toString());
            }
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_LANGUAGES_BEING_LEARNED;
    }

}
