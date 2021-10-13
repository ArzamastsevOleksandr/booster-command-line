package com.booster;

import com.booster.command.Command;
import com.booster.command.handler.HelpCommandHandler;
import com.booster.command.handler.ListLanguagesBeingLearnedCommandHandler;
import com.booster.command.handler.ListLanguagesCommandHandler;
import com.booster.command.handler.UnrecognizedCommandHandler;
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
    private final UnrecognizedCommandHandler unrecognizedCommandHandler;

    private final CommonOperations commonOperations;

    public void launch() {
        commonOperations.greeting();
        commonOperations.help();
        commonOperations.askForInput();

        Command command = nextCommand();
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
                case UNRECOGNIZED:
                    unrecognizedCommandHandler.handle();
                    break;
            }
            commonOperations.askForInput();
            command = nextCommand();
        }
        commonOperations.end();
    }

    private Command nextCommand() {
        String line = commandLineReader.readLine();
        return Command.fromString(line);
    }

}
