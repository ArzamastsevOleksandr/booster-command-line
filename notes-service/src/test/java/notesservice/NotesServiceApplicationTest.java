package notesservice;

import api.exception.NotFoundException;
import api.notes.AddNoteInput;
import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import api.tags.TagDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import notesservice.feign.TagServiceClient;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

// todo: should this test directly depend on the api module? Should the api be mocked?
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class NotesServiceApplicationTest {

    @MockBean
    TagServiceClient tagServiceClient;
    @Autowired
    TagIdRepository tagIdRepository;
    @Autowired
    NoteService noteService;
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    TestNoteService testNoteService;
    @Autowired
    NoteRepository noteRepository;

    @Test
    void shouldReturn404WhenNoteByIdNotFound() {
        System.out.println("aaa: " + HttpStatus.NOT_FOUND);
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
        Long id = testNoteService.createNote(content);
        // then
        webTestClient.get()
                .uri("/notes/" + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.content").isEqualTo(content);
    }

    @Test
    void shouldCreateNote() {
        // given
        var content = "Test Note";
        var input = new AddNoteInput(content);
        // when
        webTestClient.post()
                .uri("/notes/")
                .bodyValue(input)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody(NoteDto.class)
                .consumeWith(response -> {
                    NoteDto noteDto = response.getResponseBody();
                    // then
                    assertThat(noteDto.id()).isNotNull();
                    assertThat(noteDto.content()).isEqualTo(content);

                    assertThat(noteRepository.findById(noteDto.id())).isNotEmpty();
                });
    }

    @Test
    void shouldReturnAllNotes() {
        // given
        var content1 = "Test Note 1";
        var content2 = "Test Note 2";
        // when
        Long id1 = testNoteService.createNote(content1);
        Long id2 = testNoteService.createNote(content2);
        // then
        webTestClient.get()
                .uri("/notes/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody(Object[].class)
                .consumeWith(response -> {
                    Object[] items = response.getResponseBody();
                    var objectMapper = new ObjectMapper();
                    List<NoteDto> noteDtos = Arrays.stream(items).map(item -> objectMapper.convertValue(item, NoteDto.class)).toList();
                    assertThat(noteDtos).containsExactlyInAnyOrder(new NoteDto(id1, content1), new NoteDto(id2, content2));
                });
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
        var content = "Test Note";
        Long id = testNoteService.createNote(content);

        assertThat(noteRepository.findById(id)).isNotEmpty();
        // when
        webTestClient.delete()
                .uri("/notes/" + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        // then
        assertThat(noteRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldAddTagsToNote() {
        // given
        var content = "slowly is the fastest way";
        var education = "education";
        var passion = "passion";

        long tagId1 = 1L;
        long tagId2 = 2L;

        NoteDto noteDto = noteService.add(new AddNoteInput(content));

        when(tagServiceClient.findByName(education))
                .thenReturn(TagDto.builder().id(tagId1).name(education).build());
        when(tagServiceClient.findByName(passion))
                .thenReturn(TagDto.builder().id(tagId2).name(passion).build());
        // when
        webTestClient.post()
                .uri("/notes/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.id())
                        .tagNames(Set.of(education, passion))
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(noteDto.id())
                .jsonPath("$.content").isEqualTo(noteDto.content());
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
        var content = "slowly is the fastest way";
        var education = "education";

        NoteDto noteDto = noteService.add(new AddNoteInput(content));

        when(tagServiceClient.findByName(education))
                .thenThrow(new NotFoundException("Tag not found by name: " + education));
        // when
        webTestClient.post()
                .uri("/notes/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.id())
                        .tagNames(Set.of(education))
                        .build())
                .exchange()
                // then
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/notes/add-tags/")
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Tag not found by name: " + education);
    }

}
