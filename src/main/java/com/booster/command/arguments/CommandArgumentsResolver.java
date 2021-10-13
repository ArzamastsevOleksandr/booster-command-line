package com.booster.command.arguments;

import com.booster.command.Command;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommandArgumentsResolver {

    public CommandWithArguments resolve(String line) {
        List<String> commandWithArguments = Arrays.stream(line.split(" "))
                .filter(s -> ! s.isBlank())
                .collect(Collectors.toList());

        String commandString = commandWithArguments.get(0);
        Command command = Command.fromString(commandString);

        if (Command.isRecognizable(command)) {
            return CommandWithArguments.builder()
                    .command(command)
                    .arguments(commandWithArguments.subList(1, commandWithArguments.size()))
                    .build();
        } else {
            return CommandWithArguments.builder()
                    .command(Command.UNRECOGNIZED)
                    .build();
        }
    }

}
