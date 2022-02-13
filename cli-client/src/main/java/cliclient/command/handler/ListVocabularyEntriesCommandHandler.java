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

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;
    private final VocabularyEntryControllerApiClient vocabularyEntryClient;
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
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryClient.findById(id);
        showAndUpdateLastSeenAt(vocabularyEntryDto);
    }

    private void showAndUpdateLastSeenAt(VocabularyEntryDto vocabularyEntryDto) {
        adapter.writeLine(vocabularyEntryDto);
        vocabularyEntryService.updateLastSeenAt(vocabularyEntryDto);
    }

    private void displayVocabularyEntries(ListVocabularyEntriesCommandArgs args) {
//        // todo: pagination
//        Collection<VocabularyEntryDto> vocabularyEntryDtos = vocabularyEntryControllerApiClient.findFirst(vocabularyFetchSize);
//        if (CollectionUtils.isEmpty(vocabularyEntryDtos)) {
//            adapter.error("There are no vocabulary entries yet");
//        } else {
//            vocabularyEntryDtos.forEach(this::showAndUpdateLastSeenAt);
//        }
//        args.substring().ifPresent(s -> {
//            adapter.writeLine("With substr: " + s + ": " + vocabularyEntryControllerApiClient.countWithSubstring(s));
//        });
        args.substring().ifPresentOrElse(substring -> {

        }, () -> {
            var paginator = new Paginator<VocabularyEntryDto>(args.pagination(), vocabularyEntryClient.countAll()) {
                List<VocabularyEntryDto> nextBatch() {
                    return vocabularyEntryClient.findFirst(limit());
                }
            };
            display(paginator);
        });
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

    private static abstract class Paginator<T> {
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

        String counter() {
            return endInclusive + "/" + count;
        }

        abstract List<T> nextBatch();

        List<T> nextBatchAndUpdateRange() {
            List<T> nextBatch = nextBatch();
            updateRange();
            return nextBatch;
        }
    }

    private void display(Paginator<VocabularyEntryDto> paginator) {
        displayAndUpdateLastSeenAt(paginator);

        String line = readLineIfInRangeOrEnd(paginator);
        while (!line.equals("e") && paginator.isInRange()) {
            displayAndUpdateLastSeenAt(paginator);
            line = readLineIfInRangeOrEnd(paginator);
        }
    }

    private String readLineIfInRangeOrEnd(Paginator<VocabularyEntryDto> paginator) {
        return paginator.isInRange() ? adapter.readLine() : "e";
    }

    private void displayAndUpdateLastSeenAt(Paginator<VocabularyEntryDto> paginator) {
        adapter.writeLine(paginator.counter());
        // todo: color + use 1 batch request to update last seen at
        paginator.nextBatchAndUpdateRange().forEach(this::showAndUpdateLastSeenAt);
    }

}
