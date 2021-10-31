package com.booster;

import com.booster.adapter.CommandLineAdapter;
import com.booster.adapter.CommonOperations;
import com.booster.command.Command;
import com.booster.command.arguments.CommandArgumentsValidator;
import com.booster.command.arguments.CommandWithArguments;
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
        String line = adapter.readLine();
        return commandArgumentsValidator.validate(line);
    }

}
