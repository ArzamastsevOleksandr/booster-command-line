package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.Args;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.booster.command.Command.ADD_LANGUAGE_BEING_LEARNED;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedArgsResolver {

    private final LanguageDao languageDao;

    public CommandWithArguments resolve(List<String> args) {
        var builder = getBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            String[] flagAndValue = Args.splitAndStrip(args.get(0));
            checkIfFlagHasValue(flagAndValue);

            checkIfFlagIsId(flagAndValue);

            String idValue = flagAndValue[1];
            checkIfLanguageIdIsCorrectNumber(idValue);
            long languageId = Long.parseLong(idValue);
            checkIfLanguageExistsWithId(languageId);

            return builder
                    .args(new AddLanguageBeingLearnedArgs(languageId))
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

    private String commandString() {
        return ADD_LANGUAGE_BEING_LEARNED.extendedToString();
    }

    private void checkIfFlagHasValue(String[] flagAndValue) {
        if (flagAndValue.length != 2) {
            List<String> argErrors = List.of("Flag must come together with the value, separated by the '=' sign.");
            throw new ArgsValidationException(argErrors);
        }
    }

    private void checkIfFlagIsId(String[] flagAndValue) {
        String idFlag = flagAndValue[0];
        if (!idFlag.equals("id")) {
            List<String> argErrors = List.of("Unknown flag: " + idFlag + " for the " + commandString() + " command.");
            throw new ArgsValidationException(argErrors);
        }
    }

    private void checkIfLanguageIdIsCorrectNumber(String idValue) {
        if (isNotLongType(idValue)) {
            List<String> argErrors = List.of("Language id must be a positive integer number. Provided: " + idValue + ".");
            throw new ArgsValidationException(argErrors);
        }
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
