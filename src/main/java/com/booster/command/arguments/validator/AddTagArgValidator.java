package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.ADD_TAG;

@Component
@RequiredArgsConstructor
public class AddTagArgValidator implements ArgValidator {

    private final TagService tagService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresentOrElse(this::checkIfTagAlreadyExists, NAME_IS_MISSING);
        return commandWithArgs;
    }

    private void checkIfTagAlreadyExists(String tag) {
        if (tagService.existsWithName(tag)) {
            throw new ArgsValidationException("Tag already exists with name: " + tag);
        }
    }

    @Override
    public Command command() {
        return ADD_TAG;
    }

}
