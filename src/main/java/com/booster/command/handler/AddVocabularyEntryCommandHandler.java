package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.Settings;
import com.booster.model.Word;
import com.booster.service.SettingsService;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordService wordService;
    private final SettingsService settingsService;

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

        vocabularyEntryDao.addWithDefaultValues(params);
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
