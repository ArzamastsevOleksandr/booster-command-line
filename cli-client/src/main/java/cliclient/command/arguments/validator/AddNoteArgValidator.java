package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.ADD_NOTE;

@Component
@RequiredArgsConstructor
public class AddNoteArgValidator implements ArgValidator {

    private final TagService tagService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getContent().isEmpty()) {
            throw new ArgsValidationException("Content is missing");
        }
        commandWithArgs.getTag().ifPresent(this::checkIfTagExists);
        return commandWithArgs;
    }

    private void checkIfTagExists(String tag) {
        if (!tagService.existsWithName(tag)) {
            throw new ArgsValidationException("Tag does not exist: " + tag);
        }
    }

    @Override
    public Command command() {
        return ADD_NOTE;
    }

}
