package com.booster.command.service;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.resolver.ArgsResolver;
import com.booster.output.CommandLineWriter;
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
    private final CommandLineWriter commandLineWriter;

    @Autowired
    public ArgumentsResolverCollectionService(List<ArgsResolver> argResolvers, CommandLineWriter commandLineWriter) {
        this.commandLineWriter = commandLineWriter;
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
        commandLineWriter.writeLine("Registered the following command arg resolvers: ");
        commandLineWriter.newLine();

        argResolvers.keySet()
                .forEach(command -> commandLineWriter.writeLine(command.toString()));
        commandLineWriter.newLine();
    }


}
