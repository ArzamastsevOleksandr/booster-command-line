package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.model.Vocabulary;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabulariesCommandHandler {

    private final VocabularyDao vocabularyDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        List<Vocabulary> vocabularies = vocabularyDao.findAll();

        if (vocabularies.isEmpty()) {
            commandLineWriter.writeLine("There are no vocabularies yet.");
        } else {
            commandLineWriter.writeLine("All vocabularies:");
            for (var vocabulary : vocabularies) {
                commandLineWriter.writeLine(vocabulary.toString());
            }
        }
    }

}
