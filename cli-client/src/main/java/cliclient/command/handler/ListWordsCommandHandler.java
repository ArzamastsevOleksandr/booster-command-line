package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.model.Word;
import cliclient.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListWordsCommandHandler implements CommandHandler {

    private final WordService wordService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
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
