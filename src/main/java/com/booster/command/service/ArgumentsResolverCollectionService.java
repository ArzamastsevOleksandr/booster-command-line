package com.booster.command.service;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.resolver.ArgsResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Service
public class ArgumentsResolverCollectionService {

    private static final CommandWithArguments UNRECOGNIZED = CommandWithArguments.builder()
            .command(Command.UNRECOGNIZED)
            .build();

    private final Map<Command, ArgsResolver> argResolvers;
    private final CommandLineAdapter adapter;

    @Autowired
    public ArgumentsResolverCollectionService(List<ArgsResolver> argResolvers, CommandLineAdapter adapter) {
        this.adapter = adapter;
        this.argResolvers = argResolvers.stream()
                .map(ch -> Map.entry(ch.command(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public CommandWithArguments resolve(Command command, List<String> args) {
        return Optional.ofNullable(argResolvers.get(command))
                .map(ar -> ar.resolve(args))
                .orElse(UNRECOGNIZED);
    }

    //    @PostConstruct
    public void postConstruct() {
        adapter.writeLine("Registered the following command arg resolvers: ");
        adapter.newLine();

        argResolvers.keySet()
                .forEach(command -> adapter.writeLine(command.toString()));
        adapter.newLine();
    }


}
