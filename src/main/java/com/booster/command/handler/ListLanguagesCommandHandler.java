package com.booster.command.handler;

import com.booster.dao.LanguageDao;
import com.booster.model.Language;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler {

    private final LanguageDao languageDao;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    public void handle() {
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
    }

}
