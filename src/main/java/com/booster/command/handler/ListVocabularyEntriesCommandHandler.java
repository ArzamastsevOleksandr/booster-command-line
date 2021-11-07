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
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;

    // todo: assert that pagination is present always
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
                var p = new Paginator(pagination, vocabularyEntryDao.countWithSubstring(substring));
                displayWithParameters(p, () -> vocabularyEntryDao.findAllInRangeWithSubstring(p.startInclusive, p.endInclusive, substring));
            }, () -> {
                var p = new Paginator(pagination, vocabularyEntryDao.countTotal());
                displayWithParameters(p, () -> vocabularyEntryDao.findAllInRange(p.startInclusive, p.endInclusive));
            });
        }, () -> {
            commandWithArgs.getSubstring().ifPresentOrElse(substring -> {
                vocabularyEntryDao.findAllWithSubstring(substring).forEach(adapter::writeLine);
            }, () -> {
                vocabularyEntryDao.findAll().forEach(adapter::writeLine);
            });
        });
    }

    private static class Paginator {
        int pagination, count, endInclusive;
        int startInclusive = 1;

        Paginator(int pagination, int count) {
            this.pagination = pagination;
            this.count = count;
            this.endInclusive = Math.min(this.startInclusive + this.pagination - 1, this.count);
        }

        boolean isInRange() {
            return endInclusive < count;
        }

        void updateRange() {
            startInclusive = endInclusive + 1;
            endInclusive = Math.min(endInclusive + pagination, count);
        }
    }

    private void displayWithParameters(Paginator p, Supplier<List<VocabularyEntry>> supplier) {
        List<VocabularyEntry> allInRange = supplier.get();
        displayCounter(p);
        allInRange.forEach(adapter::writeLine);

        String line = adapter.readLine();
        while (!line.equals("e") && p.isInRange()) {
            p.updateRange();
            allInRange = supplier.get();
            displayCounter(p);
            allInRange.forEach(adapter::writeLine);
            line = adapter.readLine();
        }
    }

    private void displayCounter(Paginator p) {
        adapter.writeLine(p.endInclusive + "/" + p.count);
    }

}
