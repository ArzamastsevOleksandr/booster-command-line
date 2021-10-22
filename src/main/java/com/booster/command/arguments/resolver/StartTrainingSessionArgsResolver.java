package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.StartTrainingSessionArgs;
import com.booster.command.arguments.TrainingSessionMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.booster.command.Command.START_TRAINING_SESSION;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionArgsResolver implements ArgsResolver {

    private static final String MODE_FLAG = "m";

    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.containsKey(MODE_FLAG)) {
                checkIfModeValueIsCorrect(flag2value.get(MODE_FLAG));
                return builder
                        .args(new StartTrainingSessionArgs(TrainingSessionMode.fromString(flag2value.get(MODE_FLAG))))
                        .build();
            } else {
                return builder
                        .args(new StartTrainingSessionArgs(TrainingSessionMode.fromString(TrainingSessionMode.FULL.getMode())))
                        .build();
            }
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private void checkIfModeValueIsCorrect(String mode) {
        if (TrainingSessionMode.isUnrecognized(mode)) {
            List<String> argErrors = List.of("Unrecognized training session mode: " + mode,
                    "Available modes are: " + TrainingSessionMode.modesToString()
            );
            throw new ArgsValidationException(argErrors);
        }
    }

    @Override
    public Command command() {
        return START_TRAINING_SESSION;
    }

}
