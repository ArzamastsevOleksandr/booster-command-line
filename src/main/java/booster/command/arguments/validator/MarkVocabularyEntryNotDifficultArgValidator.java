package booster.command.arguments.validator;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static booster.command.Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultArgValidator implements ArgValidator {

    private final MarkVocabularyEntryDifficultArgValidator validator;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return validator.validate(commandWithArgs);
    }

    @Override
    public Command command() {
        return MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;
    }

}
