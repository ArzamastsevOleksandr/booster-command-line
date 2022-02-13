package cliclient.postprocessor;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Component
public class CommandWithArgsPostProcessor {

    @Value("${fetch.size.vocabulary:5}")
    private int defaultPagination;

    private static final Set<Command> PAGEABLE_COMMANDS = Set.of(
            Command.LIST_VOCABULARY_ENTRIES,
            Command.LIST_LANGUAGES,
            Command.LIST_NOTES,
            Command.LIST_TAGS
    );

    public CommandWithArgs process(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        if (isPageableCommand(command)) {
            Optional<Integer> pagination = ofNullable(commandWithArgs.getPagination());
            if (pagination.isEmpty()) {
                commandWithArgs = commandWithArgs.toBuilder().pagination(defaultPagination).build();
            }
        }
        return commandWithArgs;
    }

    private boolean isPageableCommand(Command command) {
        return PAGEABLE_COMMANDS.contains(command);
    }

}
