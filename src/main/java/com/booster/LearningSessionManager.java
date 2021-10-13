package com.booster;

import com.booster.command.Command;
import com.booster.command.arguments.CommandArgumentsResolver;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.handler.*;
import com.booster.input.CommandLineReader;
import com.booster.output.CommonOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LearningSessionManager {

    private final CommandLineReader commandLineReader;

    private final HelpCommandHandler helpCommandHandler;
    private final ListLanguagesCommandHandler listLanguagesCommandHandler;
    private final ListLanguagesBeingLearnedCommandHandler listLanguagesBeingLearnedCommandHandler;
    private final AddLanguageBeingLearnedCommandHandler addLanguageBeingLearnedCommandHandler;
    private final UnrecognizedCommandHandler unrecognizedCommandHandler;

    private final CommonOperations commonOperations;

    private final CommandArgumentsResolver commandArgumentsResolver;

    public void launch() {
        commonOperations.greeting();
        commonOperations.help();
        commonOperations.askForInput();

        CommandWithArguments commandWithArguments = nextCommandWithArguments();
        Command command = commandWithArguments.getCommand();
        while (Command.isNotExit(command)) {
            switch (command) {
                case HELP:
                    helpCommandHandler.handle();
                    break;
                case LIST_LANGUAGES:
                    listLanguagesCommandHandler.handle();
                    break;
                case LIST_LANGUAGES_BEING_LEARNED:
                    listLanguagesBeingLearnedCommandHandler.handle();
                    break;
                case ADD_LANGUAGE_BEING_LEARNED:
                    addLanguageBeingLearnedCommandHandler.handle(commandWithArguments.getArguments());
                    break;
                default:
                    unrecognizedCommandHandler.handle();
                    break;
            }
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
