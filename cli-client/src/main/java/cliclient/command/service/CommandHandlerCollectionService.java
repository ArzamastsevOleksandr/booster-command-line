package cliclient.command.service;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class CommandHandlerCollectionService {

    private final Map<Command, CommandHandler> commandHandlers;
    private final CommandLineAdapter adapter;
    private final CommandArgsService commandArgsService;

    @Autowired
    public CommandHandlerCollectionService(List<CommandHandler> commandHandlers,
                                           CommandLineAdapter adapter,
                                           CommandArgsService commandArgsService) {
        this.adapter = adapter;
        this.commandArgsService = commandArgsService;
        this.commandHandlers = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCommand(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.hasNoErrors()) {
            Command command = commandWithArgs.getCommand();
            ofNullable(commandHandlers.get(command)).ifPresentOrElse(
                    commandHandler -> {
                        CommandArgs commandArgs = commandArgsService.getCommandArgs(commandWithArgs);
                        commandHandler.handle(commandArgs);
                    }, () -> adapter.writeLine("No handler is present for the " + command.extendedToString() + " command.")
            );
        } else if (commandWithArgs.getErrors().contains("Token sequence must consist of at least one argument")) {
            // todo: use error codes
            // do nothing
            return;
        } else {
            commandWithArgs.getErrors().forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

}
