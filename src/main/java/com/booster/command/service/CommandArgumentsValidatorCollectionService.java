package com.booster.command.service;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.validator.ArgValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Service
public class CommandArgumentsValidatorCollectionService {

    private static final CommandWithArguments UNRECOGNIZED = CommandWithArguments.builder()
            .command(Command.UNRECOGNIZED)
            .build();

    private final Map<Command, ArgValidator> argValidators;
    private final CommandLineAdapter adapter;

    @Autowired
    public CommandArgumentsValidatorCollectionService(List<ArgValidator> argValidators, CommandLineAdapter adapter) {
        this.adapter = adapter;
        this.argValidators = argValidators.stream()
                .map(ch -> Map.entry(ch.command(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        return Optional.ofNullable(argValidators.get(commandWithArguments.getCommand()))
                .map(argValidator -> argValidator.validate(commandWithArguments))
                .orElse(UNRECOGNIZED);
    }

    //    @PostConstruct
    public void postConstruct() {
        adapter.writeLine("Registered the following command arg resolvers: ");
        adapter.newLine();

        argValidators.keySet()
                .forEach(command -> adapter.writeLine(command.toString()));
        adapter.newLine();
    }

}
