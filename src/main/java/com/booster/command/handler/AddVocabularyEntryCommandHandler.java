package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordService wordService;
    private final SettingsService settingsService;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.hasNoErrors()) {
            var params = new AddVocabularyEntryDaoParams();

            commandWithArgs.getName().ifPresent(name -> {
                long wordId = wordService.findByNameOrCreateAndGet(name).getId();
                params.setWordId(wordId);
            });

            commandWithArgs.getId().ifPresentOrElse(params::setLanguageId, () -> {
                settingsService.findOne()
                        .flatMap(Settings::getLanguageId)
                        .ifPresent(params::setLanguageId);
            });

            commandWithArgs.getDefinition().ifPresent(params::setDefinition);

            // todo: set synonyms and antonyms
            vocabularyEntryDao.addWithDefaultValues(params);
            adapter.writeLine("Done.");
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArgs.getErrors()
                    .forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

    private List<Long> getSynonymIds(Map<String, String> flag2value) {
        return Optional.ofNullable(flag2value.get("s"))
                .map(this::getWordIds)
                .orElse(List.of());
    }

    private List<Long> getAntonymIds(Map<String, String> flag2value) {
        return Optional.ofNullable(flag2value.get("a"))
                .map(this::getWordIds)
                .orElse(List.of());
    }

    private List<Long> getWordIds(String values) {
        return Arrays.stream(values.split(";"))
                .map(String::strip)
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toList());
    }

}
