package cliclient.command.handler;

import api.notes.NoteApi;
import api.notes.NoteDto;
import api.notes.UpdateNoteInput;
import api.tags.TagDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.UpdateNoteCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class UpdateNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteApi noteApi;

    @Override
    public void handle(CmdArgs cmdArgs) {
        var args = (UpdateNoteCmdArgs) cmdArgs;
        NoteDto noteDto = noteApi.getById(args.getId());

        var input = new UpdateNoteInput();
        input.setId(args.getId());
        input.setContent(ofNullable(args.getContent()).orElse(noteDto.getContent()));

        Set<String> tags = new HashSet<>(noteDto.getTags().stream().map(TagDto::getName).toList());

        Set<String> missingRemoveTags = args.getRemoveTags().stream().filter(tag -> !tags.contains(tag)).collect(toSet());
        if (!missingRemoveTags.isEmpty()) {
            adapter.error("Note has no tags: " + missingRemoveTags);
            adapter.error(noteDto);
        } else {
            tags.addAll(args.getAddTags());
            tags.removeAll(args.getRemoveTags());
            input.setTags(tags);
            NoteDto update = noteApi.update(input);
            adapter.writeLine(update);
        }
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return UpdateNoteCmdArgs.class;
    }

}
