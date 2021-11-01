package com.booster;

import com.booster.adapter.CommandLineAdapter;
import com.booster.adapter.CommonOperations;
import com.booster.command.Command;
import com.booster.command.arguments.CommandArgumentsValidator;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.service.CommandHandlerCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PracticeSessionLauncher {

    private final CommandHandlerCollectionService commandHandlerCollectionService;
    private final CommandArgumentsValidator commandArgumentsValidator;

    private final CommandLineAdapter adapter;
    private final CommonOperations commonOperations;

    public void launch() {
        commonOperations.greeting();
        commonOperations.help();
        commonOperations.askForInput();

        CommandWithArgs commandWithArgs = nextCommandWithArguments();
        Command command = commandWithArgs.getCommand();
        while (Command.isNotExit(command)) {
            commandHandlerCollectionService.handle(commandWithArgs);

            commonOperations.askForInput();
            commandWithArgs = nextCommandWithArguments();
            command = commandWithArgs.getCommand();
        }
        commonOperations.end();
    }

    private CommandWithArgs nextCommandWithArguments() {
        String line = adapter.readLine();
        return commandArgumentsValidator.validate(line);
    }

}
