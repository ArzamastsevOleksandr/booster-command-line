package cliclient.parser;

import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineInputTransformer {

    private final Tokenizer tokenizer;
    private final CommandWithArgsService commandWithArgsService;

    public CommandWithArgs toCommandWithArgs(String input) {
        List<Token> tokens = tokenizer.parseIntoTokens(input);
        return commandWithArgsService.toCommandWithArgs(tokens);
    }

}
