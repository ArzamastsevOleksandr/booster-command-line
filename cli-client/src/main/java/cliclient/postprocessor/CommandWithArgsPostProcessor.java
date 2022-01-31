package cliclient.postprocessor;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Component
public class CommandWithArgsPostProcessor {

    // todo: settings
    private static final int DEFAULT_PAGINATION = 5;

    private static final Set<Command> PAGEABLE_COMMANDS = Set.of(
            Command.LIST_VOCABULARY_ENTRIES,
            Command.LIST_LANGUAGES,
            Command.LIST_NOTES,
            Command.LIST_TAGS
    );

    public CommandWithArgs process(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        if (commandIsPageable(command)) {
            Optional<Integer> pagination = ofNullable(commandWithArgs.getPagination());
            if (pagination.isEmpty()) {
                commandWithArgs = commandWithArgs.toBuilder().pagination(DEFAULT_PAGINATION).build();
            }
        }
        return commandWithArgs;
    }

    private boolean commandIsPageable(Command command) {
        return PAGEABLE_COMMANDS.contains(command);
    }

}
