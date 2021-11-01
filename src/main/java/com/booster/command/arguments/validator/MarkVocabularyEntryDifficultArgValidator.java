package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.MARK_VOCABULARY_ENTRY_DIFFICULT;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        commandWithArguments.getId()
                .ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);

        return commandWithArguments;
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
