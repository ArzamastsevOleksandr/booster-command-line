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
        commandWithArgs.getPagination().ifPresentOrElse(pagination -> {
            commandWithArgs.getSubstring().ifPresentOrElse(substring -> {
                displayWithPaginationAndSubstring(pagination, substring);
            }, () -> displayWithPagination(pagination));
        }, () -> {
            commandWithArgs.getSubstring().ifPresentOrElse(substring -> {
                vocabularyEntryDao.findAllWithSubstring(substring).forEach(adapter::writeLine);
            }, () -> {
                vocabularyEntryDao.findAll().forEach(adapter::writeLine);
            });
        });
    }

    // todo: DRY
    private void displayWithPaginationAndSubstring(Integer pagination, String substring) {
        int startInclusive = 1;
        int endInclusive = startInclusive + pagination - 1;
        int count = vocabularyEntryDao.countTotal();
        List<VocabularyEntry> allInRange = vocabularyEntryDao.findAllInRangeWithSubstring(startInclusive, endInclusive, substring);
        adapter.writeLine(endInclusive + "/" + count);
        allInRange.forEach(adapter::writeLine);

        String line = adapter.readLine();
        while (!line.equals("e")) {
            startInclusive = endInclusive + 1;
            endInclusive += pagination;
            allInRange = vocabularyEntryDao.findAllInRangeWithSubstring(startInclusive, endInclusive, substring);
            adapter.writeLine(endInclusive + "/" + count);
            allInRange.forEach(adapter::writeLine);
            line = adapter.readLine();
        }
    }

    private void displayWithPagination(Integer pagination) {
        int startInclusive = 1;
        int endInclusive = startInclusive + pagination - 1;
        int count = vocabularyEntryDao.countTotal();
        List<VocabularyEntry> allInRange = vocabularyEntryDao.findAllInRange(startInclusive, endInclusive);
        adapter.writeLine(endInclusive + "/" + count);
        allInRange.forEach(adapter::writeLine);

        String line = adapter.readLine();
        // todo: stop flag
        while (!line.equals("e")) {
            startInclusive = endInclusive + 1;
            endInclusive += pagination;
            allInRange = vocabularyEntryDao.findAllInRange(startInclusive, endInclusive);
            adapter.writeLine(endInclusive + "/" + count);
            allInRange.forEach(adapter::writeLine);
            line = adapter.readLine();
        }
    }

}
