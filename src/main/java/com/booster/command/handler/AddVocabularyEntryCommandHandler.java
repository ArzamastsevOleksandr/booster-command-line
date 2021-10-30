package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.AddVocabularyEntryArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (AddVocabularyEntryArgs) commandWithArguments.getArgs();
            var params = AddVocabularyEntryDaoParams.builder()
                    .wordId(args.getWordId())
                    .languageId(args.getLanguageId())
                    .synonymIds(args.getSynonymIds())
                    .antonymIds(args.getAntonymIds())
                    .definition(args.getDefinition())
                    .build();
            vocabularyEntryDao.addWithDefaultValues(params);
            adapter.writeLine("Done.");
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

}
