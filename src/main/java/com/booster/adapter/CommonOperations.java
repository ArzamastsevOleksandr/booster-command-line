package com.booster.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonOperations {

    private final CommandLineAdapter adapter;

    public void greeting() {
        adapter.writeLine("Welcome to the Language Booster!");
    }

    public void help() {
        adapter.writeLine("Type command or 'h' to get help.");
    }

    public void askForInput() {
        adapter.write("> ");
    }

    public void end() {
        adapter.newLine();
        adapter.writeLine("See you next time!");
    }

}
