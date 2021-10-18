package com.booster.command.arguments;

import com.booster.command.Command;
import com.booster.command.arguments.resolver.AddLanguageBeingLearnedArgsResolver;
import com.booster.command.arguments.resolver.AddVocabularyArgsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommandArgumentsResolver {

    private static final CommandWithArguments UNRECOGNIZED = CommandWithArguments.builder()
            .command(Command.UNRECOGNIZED)
            .build();

    private final AddLanguageBeingLearnedArgsResolver addLanguageBeingLearnedArgsResolver;
    private final AddVocabularyArgsResolver addVocabularyArgsResolver;

    // todo: custom annotation ForHandler(.class) with reflection
    public CommandWithArguments resolve(String line) {
        List<String> commandWithArguments = parseCommandAndArguments(line);

        if (commandWithArguments.isEmpty()) {
            return UNRECOGNIZED;
        } else {
            Command command = getCommand(commandWithArguments);
            List<String> args = getArgs(commandWithArguments);

            switch (command) {
                case ADD_LANGUAGE_BEING_LEARNED:
                    return addLanguageBeingLearnedArgsResolver.resolve(args);
                case ADD_VOCABULARY:
                    return addVocabularyArgsResolver.resolve(args);

                case HELP:

                case EXIT:

                case DELETE_VOCABULARY:
                case LIST_VOCABULARIES:

                case LIST_LANGUAGES:

                case LIST_WORDS:

                case ADD_VOCABULARY_ENTRY:
                case DELETE_VOCABULARY_ENTRY:
                case LIST_VOCABULARY_ENTRIES:

                case START_TRAINING_SESSION:

                case DELETE_LANGUAGE_BEING_LEARNED:
                case LIST_LANGUAGES_BEING_LEARNED:
                    return CommandWithArguments.builder()
                            .command(command)
                            .arguments(args)
                            .build();
                default:
                    return UNRECOGNIZED;
            }
        }
    }

    private List<String> parseCommandAndArguments(String line) {
        return Arrays.stream(line.split(" "))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    private Command getCommand(List<String> commandWithArguments) {
        String commandString = commandWithArguments.get(0);
        return Command.fromString(commandString);
    }

    private List<String> getArgs(List<String> commandWithArguments) {
        return commandWithArguments.subList(1, commandWithArguments.size());
    }

}
