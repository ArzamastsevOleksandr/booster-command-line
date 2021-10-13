package com.booster.output;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonOperations {

    private final CommandLineWriter commandLineWriter;

    public void greeting() {
        commandLineWriter.writeLine("Welcome to the Language Booster!");
    }

    public void help() {
        commandLineWriter.writeLine("Type command or 'h' to get help.");
    }

    public void askForInput() {
        commandLineWriter.write("> ");
    }

    public void end() {
        commandLineWriter.writeLine("END");
    }

}
