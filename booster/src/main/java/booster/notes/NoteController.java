package booster.notes;

import api.notes.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/notes/")
@RequiredArgsConstructor
class NoteController implements NoteApi {

    private final NoteService noteService;

    @Deprecated
    @Override
    public Collection<NoteDto> getAll() {
        return noteService.findAll();
    }

    @Override
    public List<NoteDto> findFirstWithSmallestLastSeenAt(Integer limit) {
        return noteService.findFirstWithSmallestLastSeenAt(limit);
    }

    @Override
    public NoteDto getById(Long id) {
        return noteService.findById(id);
    }

    @Override
    public NoteDto add(AddNoteInput input) {
        return noteService.add(input);
    }

    @Override
    public void deleteById(Long id) {
        noteService.deleteById(id);
    }

    @Override
    public NoteDto addTags(AddTagsToNoteInput input) {
        return noteService.addTags(input);
    }

    @Override
    public Integer countAll() {
        return noteService.countAll();
    }

    @Override
    public List<NoteDto> patchLastSeenAt(PatchNoteLastSeenAtInput input) {
        return noteService.patchLastSeenAt(input);
    }

    @Override
    public NoteDto update(UpdateNoteInput input) {
        return noteService.update(input);
    }

}
