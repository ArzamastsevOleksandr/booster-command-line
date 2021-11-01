package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Settings;
import com.booster.service.LanguageService;
import com.booster.service.SettingsService;
import com.booster.service.VocabularyEntryService;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.ADD_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;
    private final SettingsService settingsService;
    private final WordService wordService;
    private final LanguageService languageService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getName().isEmpty()) {
            throw new ArgsValidationException("Name is missing");
        }
        commandWithArgs.getLanguageId().ifPresentOrElse(languageId -> {
            checkIfLanguageExistsWithId(languageId);
            long wordId = getWordIdByWordName(commandWithArgs.getName().get());
            checkIfVocabularyEntryAlreadyExistsWithWordIdForLanguageId(wordId, languageId);
        }, () -> settingsService.findOne()
                .flatMap(Settings::getLanguageId)
                .ifPresentOrElse(langId -> {
                    checkIfLanguageExistsWithId(langId);
                    long wordId = getWordIdByWordName(commandWithArgs.getName().get());
                    checkIfVocabularyEntryAlreadyExistsWithWordIdForLanguageId(wordId, langId);
                }, this::languageIdAndSettingsAreMissing));
        return commandWithArgs;
    }

    private void languageIdAndSettingsAreMissing() {
        throw new ArgsValidationException("Language id is missing and no settings with default language id exist");
    }

    @Override
    public Command command() {
        return ADD_VOCABULARY_ENTRY;
    }

    private long getWordIdByWordName(String name) {
        return wordService.findByNameOrCreateAndGet(name).getId();
    }

    private void checkIfVocabularyEntryAlreadyExistsWithWordIdForLanguageId(long wordId, long languageId) {
        if (vocabularyEntryService.existsWithWordIdAndLanguageId(wordId, languageId)) {
            throw new ArgsValidationException("Vocabulary entry already exists for language with id: " + languageId);
        }
    }

    private void checkIfLanguageExistsWithId(long languageId) {
        if (!languageService.existsWithId(languageId)) {
            throw new ArgsValidationException("Language with id: " + languageId + " does not exist.");
        }
    }

}
