package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.MARK_VOCABULARY_ENTRY_DIFFICULT;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId()
                .ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);

        return commandWithArgs;
    }

    private void checkIfVocabularyEntryExistsWithId(long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException("Vocabulary entry does not exist with id: " + id);
        }
    }

    @Override
    public Command command() {
        return MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
