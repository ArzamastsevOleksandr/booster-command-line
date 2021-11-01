package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.DELETE_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArguments validateAndReturn(CommandWithArguments commandWithArguments) {
        commandWithArguments.getId()
                .ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);

        return commandWithArguments;
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
