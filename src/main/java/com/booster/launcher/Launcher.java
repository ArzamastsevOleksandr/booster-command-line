package com.booster.launcher;

import com.booster.adapter.CommandLineAdapter;
import com.booster.adapter.CommonOperations;
import com.booster.command.Command;
import com.booster.command.arguments.CommandArgumentsValidator;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.service.CommandHandlerCollectionService;
import com.booster.preprocessor.CommandWithArgsPreprocessor;
import com.booster.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Launcher {

    private final CommandHandlerCollectionService commandHandlerCollectionService;
    private final CommandArgumentsValidator commandArgumentsValidator;
    private final CommandLineAdapter adapter;
    private final CommonOperations commonOperations;
    private final CommandWithArgsPreprocessor preprocessor;
    private final SessionTrackerService sessionTrackerService;

    public void launch() {
        commonOperations.greeting();
        commonOperations.help();
        commonOperations.askForInput();

        // todo: nextCommandWithArguments only returns, move real logic here (separation of concerns)
        CommandWithArgs commandWithArgs = nextCommandWithArguments();
        commandWithArgs = commandWithArgs.hasNoErrors() ? preprocessor.preprocess(commandWithArgs) : commandWithArgs;
        Command command = commandWithArgs.getCommand();
        while (Command.isNotExit(command)) {
            handleCommandWithArguments(commandWithArgs);

            commonOperations.askForInput();
            commandWithArgs = nextCommandWithArguments();
            commandWithArgs = commandWithArgs.hasNoErrors() ? preprocessor.preprocess(commandWithArgs) : commandWithArgs;
            command = commandWithArgs.getCommand();
        }
        sessionTrackerService.getStatistics().ifPresentOrElse(
                adapter::writeLine,
                () -> adapter.writeLine("No significant activity observed")
        );
        commonOperations.end();
    }

    private void handleCommandWithArguments(CommandWithArgs commandWithArgs) {
        try {
            commandHandlerCollectionService.handle(commandWithArgs);
        } catch (Throwable t) {
            adapter.writeLine("Error: " + t.getMessage());
        }
    }

    // todo: SRP + naming
    private CommandWithArgs nextCommandWithArguments() {
        String line = adapter.readLine();
        return commandArgumentsValidator.validate(line);
    }

}
