package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.arguments.TrainingSessionMode;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.START_TRAINING_SESSION;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    // todo: pass enum directly
    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getMode().isEmpty()) {
            checkIfEntriesExistForMode(TrainingSessionMode.getDefaultMode());
            return commandWithArgs.toBuilder().mode(TrainingSessionMode.getDefaultMode().getMode()).build();
        }
        checkIfModeValueIsCorrect(commandWithArgs.getMode().get());
        checkIfEntriesExistForMode(TrainingSessionMode.fromString(commandWithArgs.getMode().get()));
        return commandWithArgs;
    }

    private void checkIfEntriesExistForMode(TrainingSessionMode mode) {
        if (!vocabularyEntryService.existAnyForTrainingMode(mode)) {
            throw new ArgsValidationException("No entries exist for mode: " + mode);
        }
    }

    private void checkIfModeValueIsCorrect(String mode) {
        if (TrainingSessionMode.isUnrecognized(mode)) {
            throw new ArgsValidationException(
                    "Unrecognized training session mode: " + mode,
                    "Available modes are: " + TrainingSessionMode.modesToString());
        }
    }

    @Override
    public Command command() {
        return START_TRAINING_SESSION;
    }

}
