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

    // todo: foreign key constraint "vocabulary_entry_vocabulary_id_fkey" on table "vocabulary_entry"
    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();

        String name = "";
        long lblId = -1;
        for (var arg : arguments) {
            String[] flagAndValue = arg.split("=");
            String flag = flagAndValue[0];
            String value = flagAndValue[1];

            switch (flag) {
                case "n":
                    name = value;
                    break;
                case "id":
                    lblId = Long.parseLong(value);
                    break;
                default:
                    // todo: proper error message
                    System.out.println("ERROR");
            }
        }
        var vocabulary = Vocabulary.builder()
                .name(name)
                .languageBeingLearnedId(lblId)
                .build();

        vocabularyDao.add(vocabulary);

        commandLineWriter.writeLine("Done");
    }

}
