package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.booster.command.Command.ADD_NOTE;

@Component
@RequiredArgsConstructor
public class AddNoteArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        Optional<String> optionalContent = commandWithArgs.getContent();
        if (optionalContent.isEmpty()) {
            throw new ArgsValidationException("Content is missing");
        }
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return ADD_NOTE;
    }

}
