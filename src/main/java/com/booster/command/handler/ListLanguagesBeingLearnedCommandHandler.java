package com.booster.command.handler;

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

    public void handle() {
        List<LanguageBeingLearned> languagesBeingLearned = languageBeingLearnedDao.findAll();

        if (languagesBeingLearned.isEmpty()) {
            commandLineWriter.writeLine("You are not learning any languages now.");
        } else {
            commandLineWriter.writeLine("All languages being learned:");
            commandLineWriter.newLine();

            for (var languageBeingLearned : languagesBeingLearned) {
                commandLineWriter.writeLine(languageBeingLearned.toString());
            }
        }
    }

}
