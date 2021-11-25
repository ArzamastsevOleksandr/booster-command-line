package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.model.VocabularyEntry;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

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
        vocabularyEntryService.findById(id).ifPresent(vocabularyEntry -> {
            adapter.writeLine(vocabularyEntry);
            vocabularyEntryService.updateLastSeenAtById(vocabularyEntry.getId());
        });
    }

    private void displayAllVocabularyEntries(CommandWithArgs commandWithArgs) {
        commandWithArgs.getPagination().ifPresentOrElse(pagination -> {
            commandWithArgs.getSubstring().ifPresentOrElse(substring -> {
                var p = new Paginator(pagination, vocabularyEntryService.countWithSubstring(substring));
                displayWithParameters(p, () -> vocabularyEntryService.findAllInRangeWithSubstring(p.startInclusive, p.endInclusive, substring));
            }, () -> {
                var p = new Paginator(pagination, vocabularyEntryService.countTotal());
                displayWithParameters(p, () -> vocabularyEntryService.findAllInRange(p.startInclusive, p.endInclusive));
            });
        }, () -> {
            commandWithArgs.getSubstring()
                    .ifPresentOrElse(
                            substring -> displayAllAtOnce(vocabularyEntryService.findAllWithSubstring(substring)),
                            () -> displayAllAtOnce(vocabularyEntryService.findAll()));
        });
    }

    private void displayAllAtOnce(List<VocabularyEntry> entries) {
        entries.forEach(adapter::writeLine);
        updateLastSeenAtByIds(entries);
    }

    private void updateLastSeenAtByIds(List<VocabularyEntry> entries) {
        vocabularyEntryService.updateLastSeenAtByIds(entries.stream().map(VocabularyEntry::getId).collect(toList()));
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
        updateLastSeenAtByIds(allInRange);

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
