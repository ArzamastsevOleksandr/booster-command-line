package com.booster.command.handler;

import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler {

    private final CommandLineWriter commandLineWriter;

    public void handle() {
        commandLineWriter.writeLine("Commands are: h e");
    }

}
