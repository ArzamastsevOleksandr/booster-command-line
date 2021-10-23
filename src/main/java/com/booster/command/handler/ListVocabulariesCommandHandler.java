package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListVocabulariesArgs;
import com.booster.dao.VocabularyDao;
import com.booster.model.Vocabulary;
import com.booster.output.CommandLineWriter;
import com.booster.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabulariesCommandHandler implements CommandHandler {

    private final VocabularyDao vocabularyDao;
    private final VocabularyService vocabularyService;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ListVocabulariesArgs) commandWithArguments.getArgs();

            args.getId().ifPresentOrElse(
                    this::displayVocabularyById,
                    this::displayAllVocabularies
            );
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    private void displayVocabularyById(Long id) {
        commandLineWriter.writeLine(vocabularyService.findById(id).get().toString());
    }

    private void displayAllVocabularies() {
        List<Vocabulary> vocabularies = vocabularyDao.findAll();

        if (vocabularies.isEmpty()) {
            commandLineWriter.writeLine("There are no vocabularies yet.");
        } else {
            commandLineWriter.writeLine("All vocabularies:");
            commandLineWriter.newLine();
            for (var vocabulary : vocabularies) {
                commandLineWriter.writeLine(vocabulary.toString());
            }
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARIES;
    }

}
