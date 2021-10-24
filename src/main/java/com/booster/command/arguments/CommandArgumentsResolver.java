package com.booster.command.arguments;

import com.booster.command.Command;
import com.booster.command.service.ArgumentsResolverCollectionService;
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

    private static final CommandWithArguments EXIT = CommandWithArguments.builder()
            .command(Command.EXIT)
            .build();

    private final ArgumentsResolverCollectionService argumentsResolverCollectionService;

    // todo: custom annotation ForHandler(.class) with reflection?
    public CommandWithArguments resolve(String line) {
        List<String> commandWithArguments = parseCommandAndArguments(line);
        if (commandWithArguments.isEmpty()) {
            return UNRECOGNIZED;
        }
        Command command = getCommand(commandWithArguments);
        if (Command.isExit(command)) {
            return EXIT;
        }
        List<String> args = getArgs(commandWithArguments);
        return argumentsResolverCollectionService.resolve(command, args);
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
