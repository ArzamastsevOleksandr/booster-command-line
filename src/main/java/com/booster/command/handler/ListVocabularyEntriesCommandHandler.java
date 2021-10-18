package com.booster.command.handler;

import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    public void handle() {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();

        if (vocabularyEntries.isEmpty()) {
            commandLineWriter.writeLine("There are no vocabulary entries yet.");
        } else {
            commandLineWriter.writeLine("All vocabulary entries:");
            for (var vocabularyEntry : vocabularyEntries) {
                commandLineWriter.writeLine(vocabularyEntry.toString());
            }
        }
    }

}
