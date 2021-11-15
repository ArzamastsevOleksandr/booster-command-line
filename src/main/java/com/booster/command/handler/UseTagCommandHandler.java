package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.params.AddTagToNoteDaoParams;
import com.booster.dao.params.AddTagToVocabularyEntryDaoParams;
import com.booster.model.Note;
import com.booster.model.VocabularyEntry;
import com.booster.service.NoteService;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UseTagCommandHandler implements CommandHandler {

    private final NoteService noteService;
    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getTag().ifPresent(tag -> {
            commandWithArgs.getNoteId().ifPresent(noteId -> {
                Note note = noteService.addTag(new AddTagToNoteDaoParams(tag, noteId));
                adapter.writeLine(note);
            });
            commandWithArgs.getVocabularyEntryId().ifPresent(veId -> {
                VocabularyEntry vocabularyEntry = vocabularyEntryService.addTag(new AddTagToVocabularyEntryDaoParams(tag, veId));
                adapter.writeLine(vocabularyEntry);
            });
        });
    }

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
