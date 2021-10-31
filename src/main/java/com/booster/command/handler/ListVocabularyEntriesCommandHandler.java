package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final VocabularyEntryService vocabularyEntryService;

    private final CommandLineAdapter adapter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            commandWithArguments.getId()
                    .ifPresentOrElse(this::displayVocabularyEntryById, this::displayAllVocabularyEntries);
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARY_ENTRIES;
    }

    private void displayVocabularyEntryById(Long id) {
        adapter.writeLine(vocabularyEntryService.findById(id).get().toString());
    }

    private void displayAllVocabularyEntries() {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();

        if (vocabularyEntries.isEmpty()) {
            adapter.writeLine("There are no vocabulary entries yet.");
        } else {
            adapter.writeLine("All vocabulary entries:");
            adapter.newLine();
            for (var vocabularyEntry : vocabularyEntries) {
                adapter.writeLine(vocabularyEntry.toString());
            }
        }
    }

}
