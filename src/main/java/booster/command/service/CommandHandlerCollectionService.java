package booster.command.service;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.CommandWithArgs;
import booster.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            Optional.ofNullable(commandHandlers.get(command)).ifPresentOrElse(
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

    //    @PostConstruct
    public void postConstruct() {
        adapter.writeLine("Registered the following commands: ");
        adapter.newLine();

        commandHandlers.keySet().forEach(adapter::writeLine);
        adapter.newLine();
    }

}
