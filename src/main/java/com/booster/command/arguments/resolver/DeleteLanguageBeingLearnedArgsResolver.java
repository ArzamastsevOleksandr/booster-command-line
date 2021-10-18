package com.booster.command.arguments.resolver;

import com.booster.command.arguments.Args;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.DeleteLanguageBeingLearnedArgs;
import com.booster.dao.LanguageBeingLearnedDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.booster.command.Command.ADD_VOCABULARY;
import static com.booster.command.Command.DELETE_LANGUAGE_BEING_LEARNED;

@Component
@RequiredArgsConstructor
public class DeleteLanguageBeingLearnedArgsResolver {

    private static final String ID_FLAG = "id";

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfLanguageBeingLearnedExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));

            return builder
                    .args(new DeleteLanguageBeingLearnedArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(DELETE_LANGUAGE_BEING_LEARNED);
    }

    private void checkIfArgumentsAreSpecified(List<String> args) {
        if (args.size() == 0) {
            throw new ArgsValidationException(List.of("No args specified for the " + commandString() + " command."));
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

    private void checkIfLanguageBeingLearnedExistsWithId(long id) {
        if (!languageBeingLearnedDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Language being learned with id: " + id + " does not exist."));
        }
    }

    private String commandString() {
        return ADD_VOCABULARY.extendedToString();
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
