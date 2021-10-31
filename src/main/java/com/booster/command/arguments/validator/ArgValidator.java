package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.Args;
import com.booster.command.arguments.CommandWithArguments;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ArgValidator {

    @Deprecated
    default CommandWithArguments validate(List<String> args) {
        return null;
    }

    CommandWithArguments validate(CommandWithArguments commandWithArguments);

    Command command();

    //    todo: rename
    default CommandWithArguments.CommandWithArgumentsBuilder getCommandBuilder() {
        return CommandWithArguments.builder()
                .command(command());
    }

    default Map<String, String> checkFlagsWithValuesAndReturn(List<String> args) {
        try {
            return args.stream()
                    .map(Args::splitAndStrip)
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            List<String> argErrors = List.of(
                    "Flag must come together with the value, separated by the '=' sign.",
                    "All flag-value pairs must be separated with a space."
            );
            throw new ArgsValidationException(argErrors);
        }
    }

    default void checkIfMandatoryFlagsArePresent(Map<String, String> flag2value, Set<String> mandatoryFlags) {
        var providedFlags = new HashSet<>(flag2value.keySet());
        var mandatoryFlagsCopy = new HashSet<>(mandatoryFlags);
        mandatoryFlagsCopy.removeAll(providedFlags);
        if (!mandatoryFlagsCopy.isEmpty()) {
            var argErrors = List.of(
                    "Mandatory flags (" + String.join(",", mandatoryFlagsCopy) + ") are missing"
            );
            throw new ArgsValidationException(argErrors);
        }
    }

}
