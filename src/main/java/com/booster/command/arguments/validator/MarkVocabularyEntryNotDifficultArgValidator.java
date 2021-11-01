package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultArgValidator implements ArgValidator {

    private final MarkVocabularyEntryDifficultArgValidator validator;

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        return validator.validate(commandWithArguments);
    }

    @Override
    public Command command() {
        return MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;
    }

}
