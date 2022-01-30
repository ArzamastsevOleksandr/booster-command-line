package cliclient.command.handler;

import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListVocabularyEntriesCommandArgs;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;
    private final CommandLineAdapter adapter;

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
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryControllerApiClient.findById(id);
        showAndUpdateLastSeenAt(vocabularyEntryDto);
    }

    private void showAndUpdateLastSeenAt(VocabularyEntryDto vocabularyEntryDto) {
        adapter.writeLine(vocabularyEntryDto);
        vocabularyEntryService.updateLastSeenAt(vocabularyEntryDto);
    }

    private void displayVocabularyEntries(ListVocabularyEntriesCommandArgs args) {
        // todo: pagination
        Collection<VocabularyEntryDto> vocabularyEntryDtos = vocabularyEntryControllerApiClient.getAll();
        if (CollectionUtils.isEmpty(vocabularyEntryDtos)) {
            adapter.error("There are no vocabulary entries yet");
        } else {
            vocabularyEntryDtos.forEach(this::showAndUpdateLastSeenAt);
        }
//        args.pagination().ifPresentOrElse(pagination -> {
//            args.substring().ifPresentOrElse(substring -> {
//                var p = new Paginator(pagination, vocabularyEntryService.countWithSubstring(substring));
//                display(p, () -> vocabularyEntryService.findWithSubstringLimit(substring, p.limit()));
//            }, () -> {
//                var p = new Paginator(pagination, vocabularyEntryService.countTotal());
//                display(p, () -> vocabularyEntryService.findAllLimit(p.limit()));
//            });
//        }, () -> {
//            args.substring().ifPresentOrElse(
//                    substring -> displayAllAtOnce(vocabularyEntryService.findAllWithSubstring(substring)),
//                    () -> displayAllAtOnce(vocabularyEntryService.findAll()));
//        });
    }

//    private void displayAllAtOnce(List<VocabularyEntry> entries) {
//        entries.stream().map(colorProcessor::coloredEntry).forEach(adapter::writeLine);
////        vocabularyEntryService.updateLastSeenAtByIds(entries.stream().map(VocabularyEntry::getId).collect(toList()));
//    }

    private static class Paginator {
        int pagination, count, endInclusive;
        int startInclusive = 1;

        Paginator(int pagination, int count) {
            this.pagination = pagination;
            this.count = count;
            this.endInclusive = Math.min(this.startInclusive + this.pagination - 1, this.count);
        }

        boolean isInRange() {
            return startInclusive <= count;
        }

        void updateRange() {
            startInclusive = endInclusive + 1;
            endInclusive = Math.min(endInclusive + pagination, count);
        }

        int limit() {
            int limit = endInclusive - startInclusive + 1;
            return Math.max(limit, 0);
        }
    }

//    private void display(Paginator p, Supplier<List<VocabularyEntry>> supplier) {
//        displayAndUpdateLastSeenAt(p, supplier);
//
//        String line = readLineIfInRangeOrEnd(p);
//        while (!line.equals("e") && p.isInRange()) {
//            displayAndUpdateLastSeenAt(p, supplier);
//            line = readLineIfInRangeOrEnd(p);
//        }
//    }
//
//    private String readLineIfInRangeOrEnd(Paginator p) {
//        return p.isInRange() ? adapter.readLine() : "e";
//    }
//
//    private void displayAndUpdateLastSeenAt(Paginator p, Supplier<List<VocabularyEntry>> supplier) {
//        displayCounter(p);
////        displayAllAtOnce(supplier.get());
//        p.updateRange();
//    }

    private void displayCounter(Paginator p) {
        adapter.writeLine(p.endInclusive + "/" + p.count);
    }

}
