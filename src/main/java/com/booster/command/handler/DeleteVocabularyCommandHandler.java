package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyCommandHandler {

    private final VocabularyDao vocabularyDao;

    private final CommandLineWriter commandLineWriter;

    // todo: id flag
    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();
        String id = arguments.get(0);
        vocabularyDao.delete(Long.parseLong(id));

        commandLineWriter.writeLine("Done");
        commandLineWriter.newLine();
    }

}
