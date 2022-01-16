package cliclient.launcher;

import cliclient.adapter.CommandLineAdapter;
import cliclient.adapter.CommonOperations;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgumentsValidator;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.service.CommandHandlerCollectionService;
import cliclient.preprocessor.CommandWithArgsPreprocessor;
import cliclient.service.SessionTrackerService;
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
        adapter.writeLine("Welcome to the Booster!");
        commonOperations.help();
        askForInput();

        // todo: nextCommandWithArguments only returns, move real logic here (separation of concerns)
        CommandWithArgs commandWithArgs = nextCommandWithArguments();
        commandWithArgs = commandWithArgs.hasNoErrors() ? preprocessor.preprocess(commandWithArgs) : commandWithArgs;
        Command command = commandWithArgs.getCommand();
        while (Command.isNotExit(command)) {
            handleCommandWithArguments(commandWithArgs);

            askForInput();
            commandWithArgs = nextCommandWithArguments();
            commandWithArgs = commandWithArgs.hasNoErrors() ? preprocessor.preprocess(commandWithArgs) : commandWithArgs;
            command = commandWithArgs.getCommand();
        }
        sessionTrackerService.getStatistics().ifPresentOrElse(
                adapter::writeLine,
                () -> adapter.writeLine("No significant activity observed")
        );
        adapter.newLine();
        adapter.writeLine("See you next time!");
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

    private void askForInput() {
        adapter.write(ColorCodes.purple(">> "));
    }

}
