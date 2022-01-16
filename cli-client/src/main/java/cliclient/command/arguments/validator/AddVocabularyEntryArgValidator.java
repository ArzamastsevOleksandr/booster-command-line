package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.model.Settings;
import cliclient.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.ADD_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;
    private final SettingsService settingsService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final TagService tagService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getName().isEmpty()) {
            NAME_IS_MISSING.run();
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

        commandWithArgs.getTag().ifPresent(this::checkIfTagExists);
        return commandWithArgs;
    }

    private void checkIfTagExists(String tag) {
        if (!tagService.existsWithName(tag)) {
            throw new ArgsValidationException("Tag does not exist: " + tag);
        }
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
