package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.model.Vocabulary;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddVocabularyCommandHandler {

    private final VocabularyDao vocabularyDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();
        String languageBeingLearnedId = arguments.get(0);
        String vocabularyName = arguments.get(1);

        var vocabulary = Vocabulary.builder()
                .name(vocabularyName)
                .languageBeingLearnedId(Long.parseLong(languageBeingLearnedId))
                .build();

        vocabularyDao.add(vocabulary);

        commandLineWriter.writeLine("Done");
    }

}
