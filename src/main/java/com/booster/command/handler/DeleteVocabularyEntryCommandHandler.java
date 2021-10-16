package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryCommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();
        String id = arguments.get(0);

        vocabularyEntryDao.delete(Long.parseLong(id));

        commandLineWriter.writeLine("Done");
        commandLineWriter.newLine();
    }

}
