package com.booster.command.handler;

import com.booster.dao.LanguageModelDao;
import com.booster.model.LanguageModel;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler {

    private final LanguageModelDao languageModelDao;

    private final CommandLineWriter commandLineWriter;

    public void handle() {
        List<LanguageModel> languageModels = languageModelDao.findAll();
        commandLineWriter.writeLine("All languages:");
        for (var languageModel: languageModels) {
            commandLineWriter.writeLine(languageModel.toString());
        }
    }

}
