package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.DELETE_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
//        commandWithArgs.getId()
//                .ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);

        return commandWithArgs;
    }

    @Override
    public Command command() {
        return DELETE_VOCABULARY_ENTRY;
    }

    private void checkIfVocabularyEntryExistsWithId(long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException("Vocabulary entry with id: " + id + " does not exist.");
        }
    }

}
