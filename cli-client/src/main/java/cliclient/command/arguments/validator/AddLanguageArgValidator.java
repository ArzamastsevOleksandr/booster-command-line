package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.ADD_LANGUAGE;

@Component
@RequiredArgsConstructor
public class AddLanguageArgValidator implements ArgValidator {

    private final LanguageService languageService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName()
                .map(String::toUpperCase)
                .ifPresentOrElse(this::checkIfLanguageAlreadyExistsWithName, NAME_IS_MISSING);

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
