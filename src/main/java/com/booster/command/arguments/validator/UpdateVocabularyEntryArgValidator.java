package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.UPDATE_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);
        return commandWithArgs;
    }

    private void checkIfVocabularyEntryExistsWithId(Long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException("Vocabulary entry does not exist with id: " + id);
        }
    }

    @Override
    public Command command() {
        return UPDATE_VOCABULARY_ENTRY;
    }

}
