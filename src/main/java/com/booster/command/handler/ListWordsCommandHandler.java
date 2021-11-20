package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Word;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListWordsCommandHandler implements CommandHandler {

    private final WordService wordService;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        List<Word> words = wordService.findAll();

        if (words.isEmpty()) {
            adapter.writeLine("There are no words in the system yet.");
        } else {
            adapter.writeLine("All words:");

            words.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_WORDS;
    }

}
