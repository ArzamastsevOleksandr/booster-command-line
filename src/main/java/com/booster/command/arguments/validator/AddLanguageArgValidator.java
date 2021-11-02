package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.ADD_LANGUAGE;

@Component
@RequiredArgsConstructor
public class AddLanguageArgValidator implements ArgValidator {

    private final LanguageService languageService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName()
                .map(String::toUpperCase)
                .ifPresentOrElse(this::checkIfLanguageAlreadyExistsWithName, () -> {
                    throw new ArgsValidationException("Name is missing");
                });

        return commandWithArgs;
    }

    private void checkIfLanguageAlreadyExistsWithName(String nameUpperCase) {
        if (languageService.existsWithName(nameUpperCase)) {
            throw new ArgsValidationException("Language already exists with name: " + nameUpperCase);
        }
    }

    @Override
    public Command command() {
        return ADD_LANGUAGE;
    }

}
