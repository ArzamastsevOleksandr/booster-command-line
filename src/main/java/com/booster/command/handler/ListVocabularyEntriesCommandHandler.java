package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListVocabularyEntriesArgs;
import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
import com.booster.output.CommandLineWriter;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final VocabularyEntryService vocabularyEntryService;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ListVocabularyEntriesArgs) commandWithArguments.getArgs();

            args.getId().ifPresentOrElse(
                    this::displayVocabularyEntryById,
                    this::displayAllVocabularyEntries
            );
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARY_ENTRIES;
    }

    private void displayVocabularyEntryById(Long id) {
        commandLineWriter.writeLine(vocabularyEntryService.findById(id).get().toString());
    }

    private void displayAllVocabularyEntries() {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();

        if (vocabularyEntries.isEmpty()) {
            commandLineWriter.writeLine("There are no vocabulary entries yet.");
        } else {
            commandLineWriter.writeLine("All vocabulary entries:");
            commandLineWriter.newLine();
            for (var vocabularyEntry : vocabularyEntries) {
                commandLineWriter.writeLine(vocabularyEntry.toString());
            }
        }
    }

}
