package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.UseTagCommandArgs;
import cliclient.dao.params.AddTagToVocabularyEntryDaoParams;
import cliclient.model.Note;
import cliclient.model.VocabularyEntry;
import cliclient.service.ColorProcessor;
import cliclient.service.NoteService;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UseTagCommandHandler implements CommandHandler {

    private final NoteService noteService;
    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UseTagCommandArgs) commandArgs;
        args.noteId().ifPresent(id -> {
            Note note = noteService.addTag(args.tag(), id);
            adapter.writeLine(note);
        });
        args.vocabularyEntryId().ifPresent(id -> {
            VocabularyEntry entry = vocabularyEntryService.addTag(new AddTagToVocabularyEntryDaoParams(args.tag(), id));
            adapter.writeLine(colorProcessor.coloredEntry(entry));
        });
    }

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
