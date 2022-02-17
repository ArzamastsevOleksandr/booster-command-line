package cliclient.command.service;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.handler.CommandHandler;
import cliclient.service.ColorProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class CommandHandlerCollectionService {

    private final ColorProcessor colorProcessor;
    private final Map<Command, CommandHandler> command2Handler;
    private final CommandLineAdapter adapter;
    private final CommandArgsService commandArgsService;

    @Autowired
    public CommandHandlerCollectionService(ColorProcessor colorProcessor,
                                           List<CommandHandler> commandHandlers,
                                           CommandLineAdapter adapter,
                                           CommandArgsService commandArgsService) {
        this.colorProcessor = colorProcessor;
        this.adapter = adapter;
        this.commandArgsService = commandArgsService;
        this.command2Handler = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCommand(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.hasNoErrors()) {
            handleCommandWithArgs(commandWithArgs);
        } else if (commandWithArgs.isNoInput()) {
            return;
        } else {
            commandWithArgs.getErrors().forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

    private void handleCommandWithArgs(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        ofNullable(command2Handler.get(command)).ifPresentOrElse(
                commandHandler -> {
                    CommandArgs commandArgs = commandArgsService.getCommandArgs(commandWithArgs);
                    commandHandler.handle(commandArgs);
                }, () -> adapter.error("No handler is present for the " + colorProcessor.coloredCommand(command) + " command.")
        );
    }

}
