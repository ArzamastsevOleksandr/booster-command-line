package com.booster.command;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.handler.CommandHandler;
import com.booster.output.CommandLineWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Service
public class CommandHandlerCollectionService {

    private final Map<Command, CommandHandler> commandHandlers;
    private final CommandLineWriter commandLineWriter;

    @Autowired
    public CommandHandlerCollectionService(List<CommandHandler> commandHandlers, CommandLineWriter commandLineWriter) {
        this.commandLineWriter = commandLineWriter;
        this.commandHandlers = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCommand(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CommandWithArguments commandWithArguments) {
        Command command = commandWithArguments.getCommand();
        Optional.ofNullable(commandHandlers.get(command))
                .ifPresentOrElse(
                        commandHandler -> commandHandler.handle(commandWithArguments),
                        () -> commandLineWriter.writeLine("No handler is present for the " + command.extendedToString() + " command.")
                );
    }

    //    @PostConstruct
    public void postConstruct() {
        commandLineWriter.writeLine("Registered the following commands: ");
        commandLineWriter.newLine();

        commandHandlers.keySet()
                .forEach(command -> commandLineWriter.writeLine(command.toString()));
        commandLineWriter.newLine();
    }

}
