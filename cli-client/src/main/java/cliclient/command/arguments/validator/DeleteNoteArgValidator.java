package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.DELETE_NOTE;

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
