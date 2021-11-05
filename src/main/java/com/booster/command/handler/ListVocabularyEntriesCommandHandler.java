package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
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

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId()
                .ifPresentOrElse(this::displayVocabularyEntryById, () -> displayAllVocabularyEntries(commandWithArgs));
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARY_ENTRIES;
    }

    private void displayVocabularyEntryById(Long id) {
        vocabularyEntryService.findById(id).ifPresent(adapter::writeLine);
    }

    private void displayAllVocabularyEntries(CommandWithArgs commandWithArgs) {
        commandWithArgs.getPagination().ifPresentOrElse(this::displayWithPagination, () -> {
            List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();

            if (vocabularyEntries.isEmpty()) {
                adapter.writeLine("There are no vocabulary entries yet.");
            } else {
                adapter.writeLine("All vocabulary entries:");
                adapter.newLine();

                vocabularyEntries.forEach(adapter::writeLine);
            }
        });
    }

    private void displayWithPagination(Integer pagination) {
        int offset = 1;
        int limit = offset + pagination;
        List<VocabularyEntry> allInRange = vocabularyEntryDao.findAllInRange(offset, limit);
        allInRange.forEach(adapter::writeLine);

        String line = adapter.readLine();
        while (!line.strip().equals("e")) {
            offset = limit;
            limit += pagination;
            allInRange = vocabularyEntryDao.findAllInRange(offset, limit);
            allInRange.forEach(adapter::writeLine);
            line = adapter.readLine();
        }
    }

}
