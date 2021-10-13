package com.booster.command.handler;

import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnrecognizedCommandHandler {

    private final CommandLineWriter commandLineWriter;

    public void handle() {
        commandLineWriter.writeLine("Unknown command.");
        commandLineWriter.writeLine("Type command or 'h' to get help.");
    }

}
