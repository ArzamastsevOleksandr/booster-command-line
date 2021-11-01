package com.booster.command.service;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.handler.CommandHandler;
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

    @Autowired
    public CommandHandlerCollectionService(List<CommandHandler> commandHandlers, CommandLineAdapter adapter) {
        this.adapter = adapter;
        this.commandHandlers = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCommand(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        Optional.ofNullable(commandHandlers.get(command))
                .ifPresentOrElse(
                        commandHandler -> commandHandler.handle(commandWithArgs),
                        () -> adapter.writeLine("No handler is present for the " + command.extendedToString() + " command.")
                );
    }

    //    @PostConstruct
    public void postConstruct() {
        adapter.writeLine("Registered the following commands: ");
        adapter.newLine();

        commandHandlers.keySet()
                .forEach(command -> adapter.writeLine(command.toString()));
        adapter.newLine();
    }

}
