package booster.command.arguments.validator;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static booster.command.Command.MARK_VOCABULARY_ENTRY_DIFFICULT;

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
