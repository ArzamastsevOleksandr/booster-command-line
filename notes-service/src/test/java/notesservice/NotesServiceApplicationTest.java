package notesservice;

import api.exception.NotFoundException;
import api.notes.AddNoteInput;
import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import api.tags.TagDto;
import api.tags.TagsApi;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class NotesServiceApplicationTest {

    static final String CONTENT_1 = "Slowly is the fastest way";
    static final String CONTENT_2 = "Less but better";
    static final String TAG_NAME_1 = "education";
    static final String TAG_NAME_2 = "passion";

    @MockBean
    TagsApi tagsApi;

    @Autowired
    TagIdRepository tagIdRepository;
    @Autowired
    NoteService noteService;
    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturn404WhenNoteByIdNotFound() {
        long id = 1000;
        webTestClient.get()
                .uri("/notes/" + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/notes/" + id)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Note not found by id: " + id);
    }

    @Test
    void shouldFindNoteByIdIfItExists() {
        // given
        var content = "Test Note";
        // when
        NoteDto noteDto = noteService.add(AddNoteInput.builder()
                .content(content)
                .build());
        // then
        webTestClient.get()
                .uri("/notes/" + noteDto.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(noteDto.getId())
                .jsonPath("$.content").isEqualTo(content);
    }

    @Test
    void shouldCreateNote() {
        // when
        NoteDto note = webTestClient.post()
                .uri("/notes/")
                .bodyValue(AddNoteInput.builder().content(CONTENT_1).build())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody(NoteDto.class)
                .returnResult()
                .getResponseBody();
        // then
        assertThat(note.getContent()).isEqualTo(CONTENT_1);
        assertThat(noteService.findById(note.getId())).isEqualTo(note);
    }

    @Test
    void shouldReturnAllNotes() {
        // given
        NoteDto noteDto1 = noteService.add(AddNoteInput.builder()
                .content(CONTENT_1)
                .build());
        NoteDto noteDto2 = noteService.add(AddNoteInput.builder()
                .content(CONTENT_2)
                .build());
        // when
        NoteDto[] notes = webTestClient.get()
                .uri("/notes/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody(NoteDto[].class)
                .returnResult()
                .getResponseBody();

        assertThat(List.of(noteDto1, noteDto2)).containsExactlyInAnyOrder(notes);
        // then
        List<NoteDto> notesFromDb = Arrays.stream(notes)
                .map(NoteDto::getId)
                .map(noteService::findById)
                .toList();
        assertThat(notesFromDb).containsExactlyInAnyOrder(noteDto1, noteDto2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoNotes() {
        webTestClient.get()
                .uri("/notes/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldDeleteNoteById() {
        // given
        NoteDto noteDto = noteService.add(AddNoteInput.builder()
                .content(CONTENT_1)
                .build());
        // when
        webTestClient.delete()
                .uri("/notes/" + noteDto.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        // then
        assertThatThrownBy(() -> noteService.findById(noteDto.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Note not found by id: " + noteDto.getId());
    }

    @Test
    void shouldAddTagsToNote() {
        // given
        long tagId1 = 1L;
        long tagId2 = 2L;

        NoteDto noteDto = noteService.add(AddNoteInput.builder()
                .content(CONTENT_1)
                .build());

        when(tagsApi.findByName(TAG_NAME_1))
                .thenReturn(TagDto.builder().id(tagId1).name(TAG_NAME_1).build());
        when(tagsApi.findByName(TAG_NAME_2))
                .thenReturn(TagDto.builder().id(tagId2).name(TAG_NAME_2).build());
        // when
        webTestClient.post()
                .uri("/notes/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.getId())
                        .tagNames(Set.of(TAG_NAME_1, TAG_NAME_2))
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(noteDto.getId())
                .jsonPath("$.content").isEqualTo(noteDto.getContent());
        // then
        assertThat(tagIdRepository.findAllById(Set.of(tagId1, tagId2))).hasSize(2);
    }

    @Test
    void shouldThrowExceptionIfNoteNotFound() {
        long id = 1000L;
        webTestClient.post()
                .uri("/notes/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder().noteId(id).build())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/notes/add-tags/")
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Note not found by id: " + id);
    }

    @Test
    void shouldThrowExceptionIfTagsNotFound() {
        // given
        NoteDto noteDto = noteService.add(AddNoteInput.builder()
                .content(CONTENT_1)
                .build());

        when(tagsApi.findByName(TAG_NAME_1))
                .thenThrow(new NotFoundException("Tag not found by name: " + TAG_NAME_1));
        // when
        webTestClient.post()
                .uri("/notes/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.getId())
                        .tagNames(Set.of(TAG_NAME_1))
                        .build())
                .exchange()
                // then
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/notes/add-tags/")
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Tag not found by name: " + TAG_NAME_1);
    }

}
