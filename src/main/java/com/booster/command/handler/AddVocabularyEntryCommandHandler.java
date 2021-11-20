package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.Settings;
import com.booster.model.VocabularyEntry;
import com.booster.model.Word;
import com.booster.service.SessionTrackerService;
import com.booster.service.SettingsService;
import com.booster.service.VocabularyEntryService;
import com.booster.service.WordService;
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

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        var params = new AddVocabularyEntryDaoParams();

        commandWithArgs.getName().ifPresent(name -> {
            long wordId = wordService.findByNameOrCreateAndGet(name).getId();
            params.setWordId(wordId);
        });

        commandWithArgs.getLanguageId().ifPresentOrElse(params::setLanguageId, () -> {
            settingsService.findOne()
                    .flatMap(Settings::getLanguageId)
                    .ifPresent(params::setLanguageId);
        });

        commandWithArgs.getDefinition().ifPresent(params::setDefinition);

        params.setContexts(commandWithArgs.getContexts());

        params.setSynonymIds(getWordIds(commandWithArgs.getSynonyms()));
        params.setAntonymIds(getWordIds(commandWithArgs.getAntonyms()));

        commandWithArgs.getTag().ifPresent(tag -> params.setTags(Set.of(tag)));

        VocabularyEntry vocabularyEntry = vocabularyEntryService.addWithDefaultValues(params);
        adapter.writeLine(vocabularyEntry);
        adapter.writeLine("Entries added so far: " + sessionTrackerService.getVocabularyEntriesAddedCount());
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

    private Set<Long> getWordIds(Set<String> words) {
        return words.stream()
                .filter(s -> !s.isBlank())
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toSet());
    }

}
