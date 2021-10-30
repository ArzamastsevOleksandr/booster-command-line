package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.AddSettingsArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.booster.command.Command.ADD_SETTINGS;

// todo: functional style error processing with no exceptions (Option).
//  have a list of validators that return an Option[], collect all errors
@Component
@RequiredArgsConstructor
public class AddSettingsArgsResolver implements ArgsResolver {

    private static final String LANGUAGE_ID_FLAG = "lid";

    private final LanguageService languageService;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        var commandWithArgumentsBuilder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            var addSettingsArgsBuilder = AddSettingsArgs.builder();
//            todo: FP
            if (flag2value.get(LANGUAGE_ID_FLAG) != null) {
                checkIfIdIsCorrectNumber(flag2value.get(LANGUAGE_ID_FLAG));
                checkIfLanguageBeingLearnedExistsWithId(Long.parseLong(flag2value.get(LANGUAGE_ID_FLAG)));
                addSettingsArgsBuilder = addSettingsArgsBuilder.languageId(Long.parseLong(flag2value.get(LANGUAGE_ID_FLAG)));
            }
            return commandWithArgumentsBuilder
                    .args(addSettingsArgsBuilder.build())
                    .build();
        } catch (ArgsValidationException e) {
            return commandWithArgumentsBuilder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return ADD_SETTINGS;
    }

    private void checkIfLanguageBeingLearnedExistsWithId(long id) {
        if (!languageService.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Language being learned with id: " + id + " does not exist."));
        }
    }

}
