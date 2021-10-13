package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteLanguageBeingLearnedCommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    private final CommandLineWriter commandLineWriter;

    // todo: if no record deleted - notify the user
    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();
        String id = arguments.get(0);
        languageBeingLearnedDao.delete(Long.parseLong(id));
        commandLineWriter.writeLine("Done.");
        commandLineWriter.newLine();
    }

}
