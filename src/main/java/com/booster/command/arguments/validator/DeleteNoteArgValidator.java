package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.DELETE_NOTE;

@Component
@RequiredArgsConstructor
public class DeleteNoteArgValidator implements ArgValidator {

    private final NoteService noteService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId()
                .ifPresentOrElse(this::checkIfNoteExistsWithId, ID_IS_MISSING);

        return commandWithArgs;
    }

    private void checkIfNoteExistsWithId(Long id) {
        if (!noteService.existsWithId(id)) {
            throw new ArgsValidationException("Note does not exist with id: " + id);
        }
    }

    @Override
    public Command command() {
        return DELETE_NOTE;
    }

}
