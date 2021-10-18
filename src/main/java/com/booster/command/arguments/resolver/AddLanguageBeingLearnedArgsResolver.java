package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.Args;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.booster.command.Command.ADD_LANGUAGE_BEING_LEARNED;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedArgsResolver {

    private static final String ID_FLAG = "id";

    private final LanguageDao languageDao;

    public CommandWithArguments resolve(List<String> args) {
        var builder = getBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfLanguageExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));

            return builder
                    .args(new AddLanguageBeingLearnedArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(ADD_LANGUAGE_BEING_LEARNED);
    }

    private void checkIfArgumentsAreSpecified(List<String> args) {
        if (args.size() == 0) {
            List<String> argErrors = List.of("No args specified for the " + commandString() + " command.");
            throw new ArgsValidationException(argErrors);
        }
    }

    private Map<String, String> checkFlagsWithValuesAndReturn(List<String> args) {
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

    private void checkIfMandatoryFlagsArePresent(Map<String, String> flag2value, Set<String> mandatoryFlags) {
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

    private void checkIfIdIsCorrectNumber(String idValue) {
        if (isNotLongType(idValue)) {
            throw new ArgsValidationException(List.of("Id must be a positive integer number. Got: " + idValue + "."));
        }
    }

    private String commandString() {
        return ADD_LANGUAGE_BEING_LEARNED.extendedToString();
    }

    private void checkIfLanguageExistsWithId(long languageId) {
        if (!languageDao.existsWithId(languageId)) {
            throw new ArgsValidationException(List.of("Language with id: " + languageId + " does not exist."));
        }
    }

    private boolean isNotLongType(String s) {
        try {
            Long.parseLong(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

}
