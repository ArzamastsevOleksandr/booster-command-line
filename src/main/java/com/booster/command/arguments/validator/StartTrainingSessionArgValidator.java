package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.arguments.TrainingSessionMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.START_TRAINING_SESSION;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getMode().isEmpty()) {
            return commandWithArgs.toBuilder().mode(TrainingSessionMode.FULL.getMode()).build();
        }
        checkIfModeValueIsCorrect(commandWithArgs.getMode().get());
        return commandWithArgs;
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
