package com.booster.preprocessor;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommandWithArgsPreprocessor {

    private static final int DEFAULT_PAGINATION = 5;

    public CommandWithArgs preprocess(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        switch (command) {
            case LIST_VOCABULARY_ENTRIES:
                Optional<Integer> pagination = commandWithArgs.getPagination();
                if (pagination.isEmpty()) {
                    commandWithArgs = commandWithArgs.toBuilder().pagination(DEFAULT_PAGINATION).build();
                }
                break;
        }
        return commandWithArgs;
    }

}
