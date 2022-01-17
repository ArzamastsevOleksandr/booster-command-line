package notesservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class NotesServiceApplicationTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    TestNoteService testNoteService;
    @Autowired
    NoteRepository noteRepository;

    @Test
    void shouldReturn404WhenNoteByIdNotFound() {

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

}
