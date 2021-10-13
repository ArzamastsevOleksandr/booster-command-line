package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler {

    private final CommandLineWriter commandLineWriter;

    public void handle() {
        commandLineWriter.writeLine("Available commands are:");
        commandLineWriter.newLine();

        Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .forEach(command -> commandLineWriter.writeLine(command.toString() + ": " + command.getEquivalents()));

        commandLineWriter.newLine();
    }

}
