package cliclient.launcher;

import api.exception.HttpErrorResponse;
import cliclient.adapter.CommandLineAdapter;
import cliclient.adapter.CommonOperations;
import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.service.CommandHandlerCollectionService;
import cliclient.parser.CommandLineInputTransformer;
import cliclient.preprocessor.CommandWithArgsPreprocessor;
import cliclient.service.SessionTrackerService;
import cliclient.util.ColorCodes;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Launcher {

    private final CommandHandlerCollectionService commandHandlerCollectionService;
    private final CommandLineAdapter adapter;
    private final CommonOperations commonOperations;
    private final CommandWithArgsPreprocessor preprocessor;
    private final SessionTrackerService sessionTrackerService;
    private final CommandLineInputTransformer transformer;
    private final ObjectMapper objectMapper;

    public void launch() {
        adapter.writeLine("Welcome to the Booster!");
        commonOperations.help();
        askForInput();

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
            if (t instanceof FeignException.FeignClientException) {
                try {
                    var e = (FeignException.FeignClientException) t;
                    HttpErrorResponse httpErrorResponse = objectMapper.readValue(e.contentUTF8().getBytes(), HttpErrorResponse.class);
                    adapter.error(httpErrorResponse.getMessage());
                } catch (IOException ioe) {
                    adapter.error(t.getMessage());
                    adapter.error(ioe.getMessage());
                }
            } else {
                adapter.error(t.getMessage());
            }
        }
    }

    private CommandWithArgs nextCommandWithArguments() {
        String line = adapter.readLine();
        return transformer.fromString(line);
    }

    private void askForInput() {
        adapter.write(ColorCodes.purple(">> "));
    }

}
