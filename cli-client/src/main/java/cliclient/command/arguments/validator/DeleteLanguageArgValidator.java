package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.LanguageService;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.DELETE_LANGUAGE;

@Component
@RequiredArgsConstructor
public class DeleteLanguageArgValidator implements ArgValidator {

    private final LanguageService languageService;
    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId()
                .ifPresentOrElse(id -> {
                    checkIfLanguageExistsWithId(id);
                    checkIfVocabularyEntriesExistWithLanguageId(id);
                }, ID_IS_MISSING);

        return commandWithArgs;
    }

    private void checkIfLanguageExistsWithId(Long id) {
        if (!languageService.existsWithId(id)) {
            throw new ArgsValidationException("Language does not exist with id: " + id);
        }
    }

    private void checkIfVocabularyEntriesExistWithLanguageId(Long id) {
        if (vocabularyEntryService.existAnyWithLanguageId(id)) {
            throw new ArgsValidationException("There are vocabulary entries related to this language", "Delete them first");
        }
    }

    @Override
    public Command command() {
        return DELETE_LANGUAGE;
    }

}
