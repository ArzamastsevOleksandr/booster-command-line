package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesBeingLearnedCommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    public void handle(CommandWithArguments commandWithArguments) {
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

}
