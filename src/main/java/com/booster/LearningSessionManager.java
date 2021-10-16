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
    private final DeleteLanguageBeingLearnedCommandHandler deleteLanguageBeingLearnedCommandHandler;

    private final ListVocabulariesCommandHandler listVocabulariesCommandHandler;
    private final AddVocabularyCommandHandler addVocabularyCommandHandler;
    private final DeleteVocabularyCommandHandler deleteVocabularyCommandHandler;

    private final ListWordsCommandHandler listWordsCommandHandler;

    private final ListVocabularyEntriesCommandHandler listVocabularyEntriesCommandHandler;

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
                    addLanguageBeingLearnedCommandHandler.handle(commandWithArguments);
                    break;
                case DELETE_LANGUAGE_BEING_LEARNED:
                    deleteLanguageBeingLearnedCommandHandler.handle(commandWithArguments);
                    break;

                case LIST_VOCABULARIES:
                    listVocabulariesCommandHandler.handle();
                    break;
                case ADD_VOCABULARY:
                    addVocabularyCommandHandler.handle(commandWithArguments);
                    break;
                case DELETE_VOCABULARY:
                    deleteVocabularyCommandHandler.handle(commandWithArguments);
                    break;

                case LIST_WORDS:
                    listWordsCommandHandler.handle();
                    break;

                case LIST_VOCABULARY_ENTRIES:
                    listVocabularyEntriesCommandHandler.handle();
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
