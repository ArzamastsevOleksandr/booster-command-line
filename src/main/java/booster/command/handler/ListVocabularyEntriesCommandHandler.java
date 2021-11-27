package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.ListVocabularyEntriesCommandArgs;
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
    public void handle(CommandArgs commandArgs) {
        var args = (ListVocabularyEntriesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayVocabularyEntryById, () -> displayVocabularyEntries(args));
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

    private void displayVocabularyEntries(ListVocabularyEntriesCommandArgs args) {
        args.pagination().ifPresentOrElse(pagination -> {
            args.substring().ifPresentOrElse(substring -> {
                var p = new Paginator(pagination, vocabularyEntryService.countWithSubstring(substring));
                display(p, () -> vocabularyEntryService.findWithSubstringLimit(substring, pagination));
            }, () -> {
                var p = new Paginator(pagination, vocabularyEntryService.countTotal());
                display(p, () -> vocabularyEntryService.findAllLimit(pagination));
            });
        }, () -> {
            args.substring()
                    .ifPresentOrElse(
                            substring -> displayAllAtOnce(vocabularyEntryService.findAllWithSubstring(substring)),
                            () -> displayAllAtOnce(vocabularyEntryService.findAll()));
        });
    }

    private void displayAllAtOnce(List<VocabularyEntry> entries) {
        entries.forEach(adapter::writeLine);
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

    private void display(Paginator p, Supplier<List<VocabularyEntry>> supplier) {
        displayAndUpdateLastSeenAt(p, supplier);

        String line = adapter.readLine();
        while (!line.equals("e") && p.isInRange()) {
            p.updateRange();
            displayAndUpdateLastSeenAt(p, supplier);
            line = adapter.readLine();
        }
    }

    private void displayAndUpdateLastSeenAt(Paginator p, Supplier<List<VocabularyEntry>> supplier) {
        displayCounter(p);
        displayAllAtOnce(supplier.get());
    }

    private void displayCounter(Paginator p) {
        adapter.writeLine(p.endInclusive + "/" + p.count);
    }

}
