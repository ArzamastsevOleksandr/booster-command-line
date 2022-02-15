package notesservice;

import api.exception.NotFoundException;
import api.notes.AddNoteInput;
import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import api.notes.PatchNoteLastSeenAtInput;
import api.tags.TagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notesservice.feign.TagServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class NoteService {

    private final NoteRepository noteRepository;
    private final TagIdRepository tagIdRepository;
    private final TagServiceClient tagServiceClient;

    @Deprecated
    public Collection<NoteDto> findAll() {
        return noteRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(toList());
    }

    private NoteDto toDto(NoteEntity noteEntity) {
        return NoteDto.builder()
                .id(noteEntity.getId())
                .content(noteEntity.getContent())
                .lastSeenAt(noteEntity.getLastSeenAt())
                .tags(noteEntity.getTagIds()
                        .stream()
                        .map(tagIdEntity -> tagServiceClient.findById(tagIdEntity.id))
                        .collect(toSet()))
                .build();
    }

    public NoteDto findById(Long id) {
        return noteRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Note not found by id: " + id));
    }

    @Transactional
    public NoteDto add(AddNoteInput input) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(input.getContent());
        noteEntity.setLastSeenAt(input.getLastSeenAt());
        Set<TagIdEntity> tagIdEntities = input.getTags()
                .stream()
                .map(tagServiceClient::findByName)
                .map(tagDto -> tagIdRepository.findById(tagDto.getId())
                        .orElseGet(() -> {
                            var tagIdEntity = new TagIdEntity();
                            tagIdEntity.id = tagDto.getId();
                            return tagIdRepository.saveAndFlush(tagIdEntity);
                        })).collect(toSet());
        noteEntity.setTagIds(tagIdEntities);
        noteEntity = noteRepository.save(noteEntity);
        return toDto(noteEntity);
    }

    @Transactional
    // todo: org.springframework.dao.EmptyResultDataAccessException: No class notesservice.NoteEntity entity with id 11 exists!
    public void deleteById(Long id) {
        noteRepository.deleteById(id);
    }

    @Transactional
    public NoteDto addTags(AddTagsToNoteInput input) {
        Long id = input.getNoteId();
        NoteEntity noteEntity = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note not found by id: " + id));

        // todo: an optimization to make 1 http call
        Set<TagIdEntity> tagIdEntities = input.getTagNames()
                .stream()
                .map(tagServiceClient::findByName)
                .map(TagDto::getId)
                .map(tagId -> {
                    var tagIdEntity = new TagIdEntity();
                    tagIdEntity.id = tagId;
                    return tagIdEntity;
                })
                .collect(toSet());

        noteEntity.getTagIds().addAll(tagIdRepository.saveAllAndFlush(tagIdEntities));
        return toDto(noteRepository.save(noteEntity));
    }

    public Integer countAll() {
        return noteRepository.countAllBy();
    }

    public List<NoteDto> findFirst(Integer limit) {
        return noteRepository.findFirst(limit)
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<NoteDto> patchLastSeenAt(PatchNoteLastSeenAtInput input) {
        // todo: execute in batches or have a limit as to how many ids can be processed by an endpoint
        List<NoteEntity> noteEntities = noteRepository.findAllById(input.getIds())
                .stream()
                .peek(noteEntity -> noteEntity.setLastSeenAt(input.getLastSeenAt()))
                .toList();
        return noteRepository.saveAll(noteEntities)
                .stream()
                .map(this::toDto)
                .toList();
    }

}
