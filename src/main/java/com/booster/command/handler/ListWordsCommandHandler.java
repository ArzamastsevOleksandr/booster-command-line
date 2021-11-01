package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.WordDao;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListWordsCommandHandler implements CommandHandler {

    private final WordDao wordDao;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        List<Word> words = wordDao.findAll();

        if (words.isEmpty()) {
            adapter.writeLine("There are no words in the system yet.");
        } else {
            adapter.writeLine("All words:");
            for (var word : words) {
                adapter.writeLine(word.toString());
            }
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_WORDS;
    }

}
