package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddVocabularyEntryCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.dao.params.AddVocabularyEntryDaoParams;
import cliclient.model.Settings;
import cliclient.model.VocabularyEntry;
import cliclient.model.Word;
import cliclient.service.*;
import cliclient.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final WordService wordService;
    private final SettingsService settingsService;
    private final CommandLineAdapter adapter;
    private final VocabularyEntryService vocabularyEntryService;
    private final SessionTrackerService sessionTrackerService;
    private final ColorProcessor colorProcessor;
    private final StringUtil stringUtil;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddVocabularyEntryCommandArgs) commandArgs;
        var params = new AddVocabularyEntryDaoParams();

        long wordId = wordService.findByNameOrCreateAndGet(args.getName()).getId();
        params.setWordId(wordId);

        args.languageId().ifPresentOrElse(params::setLanguageId, () -> {
            settingsService.findOne()
                    .flatMap(Settings::getLanguageId)
                    .ifPresent(params::setLanguageId);
        });
        args.definition().ifPresent(params::setDefinition);

        params.setContexts(args.getContexts());
        params.setSynonymIds(getWordIds(args.getSynonyms()));
        params.setAntonymIds(getWordIds(args.getAntonyms()));

        args.tag().ifPresent(tag -> params.setTags(Set.of(tag)));

        VocabularyEntry entry = vocabularyEntryService.addWithDefaultValues(params);
        adapter.writeLine(colorProcessor.coloredEntry(entry));
        adapter.writeLine("Entries added so far: " + sessionTrackerService.getVocabularyEntriesAddedCount());
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

    private Set<Long> getWordIds(Set<String> words) {
        return words.stream()
                .filter(stringUtil::isNotBlank)
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toSet());
    }

}
