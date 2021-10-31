package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.booster.command.Command.DELETE_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        try {
            commandWithArguments.getId()
                    .ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId,
                            () -> {
                                throw new ArgsValidationException(List.of("Id is missing"));
                            });
            return commandWithArguments;
        } catch (ArgsValidationException e) {
            return getCommandBuilder().argErrors(e.getArgErrors()).build();
        }
    }

    @Override
    public Command command() {
        return DELETE_VOCABULARY_ENTRY;
    }

    private void checkIfVocabularyEntryExistsWithId(long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary entry with id: " + id + " does not exist."));
        }
    }

}
