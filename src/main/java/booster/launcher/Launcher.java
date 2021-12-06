package booster.launcher;

import booster.adapter.CommandLineAdapter;
import booster.adapter.CommonOperations;
import booster.command.Command;
import booster.command.arguments.CommandArgumentsValidator;
import booster.command.arguments.CommandWithArgs;
import booster.command.service.CommandHandlerCollectionService;
import booster.preprocessor.CommandWithArgsPreprocessor;
import booster.service.SessionTrackerService;
import booster.util.ColorCodes;
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
