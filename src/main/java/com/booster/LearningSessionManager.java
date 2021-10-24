package com.booster;

import com.booster.command.Command;
import com.booster.command.arguments.CommandArgumentsResolver;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.service.CommandHandlerCollectionService;
import com.booster.input.CommandLineReader;
import com.booster.output.CommonOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// todo: rename
@Component
@RequiredArgsConstructor
public class LearningSessionManager {

    private final CommandHandlerCollectionService commandHandlerCollectionService;
    private final CommandArgumentsResolver commandArgumentsResolver;

    private final CommandLineReader commandLineReader;
    private final CommonOperations commonOperations;

    public void launch() {
        commonOperations.greeting();
        commonOperations.help();
        commonOperations.askForInput();

        CommandWithArguments commandWithArguments = nextCommandWithArguments();
        Command command = commandWithArguments.getCommand();
        while (Command.isNotExit(command)) {
            commandHandlerCollectionService.handle(commandWithArguments);

            commonOperations.askForInput();
            commandWithArguments = nextCommandWithArguments();
            command = commandWithArguments.getCommand();
        }
        commonOperations.end();
    }

    private CommandWithArguments nextCommandWithArguments() {
        String line = commandLineReader.readLine();
        return commandArgumentsResolver.resolve(line);
    }

}
