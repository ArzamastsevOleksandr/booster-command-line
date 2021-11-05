package com.booster.command.service;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.arguments.validator.ArgValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Service
public class CommandArgumentsValidatorCollectionService {

    private final Map<Command, ArgValidator> argValidators;
    private final CommandLineAdapter adapter;

    @Autowired
    public CommandArgumentsValidatorCollectionService(List<ArgValidator> argValidators, CommandLineAdapter adapter) {
        this.adapter = adapter;
        this.argValidators = argValidators.stream()
                .map(ch -> Map.entry(ch.command(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public CommandWithArgs validate(CommandWithArgs commandWithArgs) {
        return Optional.ofNullable(argValidators.get(commandWithArgs.getCommand()))
                .map(argValidator -> argValidator.validate(commandWithArgs))
                .orElse(commandWithArgs);
    }

    //    @PostConstruct
    public void postConstruct() {
        adapter.writeLine("Registered the following command arg resolvers: ");
        adapter.newLine();

        argValidators.keySet().forEach(adapter::writeLine);
        adapter.newLine();
    }

}
