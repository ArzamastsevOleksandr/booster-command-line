package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;

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
