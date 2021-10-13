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

    public void handle() {
        List<Language> languages = languageDao.findAll();
        commandLineWriter.writeLine("All languages:");
        for (var language : languages) {
            commandLineWriter.writeLine(language.toString());
        }
    }

}
