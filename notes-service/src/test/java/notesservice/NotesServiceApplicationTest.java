package notesservice;

import api.exception.HttpErrorResponse;
import api.notes.AddNoteInput;
import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import api.notes.PatchNoteLastSeenAtInput;
import api.tags.TagDto;
import api.tags.TagsApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

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
    @Mock
    FeignException.NotFound notFound;

    @Autowired
    TagIdRepository tagIdRepository;
    @Autowired
    NoteService noteService;
    @Autowired
    WebTestClient webTestClient;

    String baseUrl = "/notes/";

    @Test
    void returns404WhenNoteNotFoundById() {
        assertThatNoteIsNotFoundById(1000);
    }

    @Test
    void findsNoteById() {
        // when
        NoteDto noteDto = createNoteAndSave(CONTENT_1);
        // then
        webTestClient.get()
                .uri(baseUrl + noteDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(noteDto.getId())
                .jsonPath("$.content").isEqualTo(CONTENT_1)
                .jsonPath("$.lastSeenAt").isNotEmpty();
    }

    @Test
    void createsNote() {
        // when
        NoteDto note = webTestClient.post()
                .uri(baseUrl)
                .bodyValue(AddNoteInput.builder().content(CONTENT_1).build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NoteDto.class)
                .returnResult()
                .getResponseBody();
        // then
        assertThat(note.getContent()).isEqualTo(CONTENT_1);
        assertThat(noteService.findById(note.getId())).isEqualTo(note);
    }

    @Test
    void returnsAllNotes() {
        // given
        NoteDto noteDto1 = createNoteAndSave(CONTENT_1);
        NoteDto noteDto2 = createNoteAndSave(CONTENT_2);

        List<NoteDto> expectedNotes = List.of(noteDto1, noteDto2);
        // when
        List<NoteDto> responseNotes = webTestClient.get()
                .uri(baseUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NoteDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseNotes).containsExactlyElementsOf(expectedNotes);
        // then
        List<NoteDto> notesFromDb = responseNotes.stream()
                .map(NoteDto::getId)
                .map(noteService::findById)
                .toList();
        assertThat(notesFromDb).containsExactlyElementsOf(expectedNotes);
    }

    @Test
    void returnsEmptyListWhenThereAreNoNotes() {
        webTestClient.get()
                .uri(baseUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void deletesNoteById() {
        // given
        NoteDto noteDto = createNoteAndSave(CONTENT_1);
        // when
        webTestClient.delete()
                .uri(baseUrl + noteDto.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
        // then
        assertThatNoteIsNotFoundById(noteDto.getId());
    }

    @Test
    void addsTagsToNote() {
        // given
        long tagId1 = 1L;
        long tagId2 = 2L;

        NoteDto noteDto = createNoteAndSave(CONTENT_1);

        TagDto tag1 = createTag(tagId1, TAG_NAME_1);
        when(tagsApi.findByName(TAG_NAME_1)).thenReturn(tag1);
        when(tagsApi.findById(tagId1)).thenReturn(tag1);

        TagDto tag2 = createTag(tagId2, TAG_NAME_2);
        when(tagsApi.findByName(TAG_NAME_2)).thenReturn(tag2);
        when(tagsApi.findById(tagId2)).thenReturn(tag2);
        // when
        webTestClient.post()
                .uri(baseUrl + "/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.getId())
                        .tagNames(Set.of(TAG_NAME_1, TAG_NAME_2))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(noteDto.getId())
                .jsonPath("$.content").isEqualTo(noteDto.getContent())
                .jsonPath("$.tags.length()").isEqualTo(2);
        // then
        assertThat(tagIdRepository.findAllById(Set.of(tagId1, tagId2))).hasSize(2);
    }

    @Test
    void returns404IfNoteNotFoundWhenAddingTags() {
        webTestClient.post()
                .uri(baseUrl + "/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder().noteId(Long.MAX_VALUE).build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + "add-tags/")
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Note not found by id: " + Long.MAX_VALUE);
    }

    @Test
    void returns404IfTagsNotFound() throws JsonProcessingException {
        // given
        NoteDto noteDto = createNoteAndSave(CONTENT_1);
        when(tagsApi.findByName(TAG_NAME_1)).thenThrow(notFound);
        when(notFound.status()).thenReturn(HttpStatus.NOT_FOUND.value());
        var httpErrorResponse = HttpErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .path(baseUrl + "/add-tags/")
                .message("Tag not found by name: " + TAG_NAME_1)
                .build();
        when(notFound.contentUTF8()).thenReturn(new ObjectMapper().writeValueAsString(httpErrorResponse));
        // when
        webTestClient.post()
                .uri(baseUrl + "/add-tags/")
                .bodyValue(AddTagsToNoteInput.builder()
                        .noteId(noteDto.getId())
                        .tagNames(Set.of(TAG_NAME_1))
                        .build())
                .exchange()
                // then
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + "add-tags/")
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Tag not found by name: " + TAG_NAME_1);
    }

    @Test
    void returnsNotesWithSmallestLastSeenAt() {
        // given
        NoteDto noteDto1 = createNoteAndSave(CONTENT_1);
        NoteDto noteDto2 = createNoteAndSave(CONTENT_2);

        webTestClient.get()
                .uri(baseUrl + "?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(noteDto1.getId())
                .jsonPath("$[0].content").isEqualTo(noteDto1.getContent())
                .jsonPath("$[0].lastSeenAt").isNotEmpty();
        // when
        webTestClient.patch()
                .uri(baseUrl + "/patch/last-seen-at/")
                .bodyValue(PatchNoteLastSeenAtInput.builder()
                        .lastSeenAt(Timestamp.from(Instant.now()))
                        .ids(List.of(noteDto1.getId()))
                        .build())
                .exchange()
                .expectStatus().isOk();
        // then
        webTestClient.get()
                .uri(baseUrl + "?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(noteDto2.getId())
                .jsonPath("$[0].content").isEqualTo(noteDto2.getContent())
                .jsonPath("$[0].lastSeenAt").isNotEmpty();
    }

    @Test
    void countsAllNotes() {
        // given
        createNoteAndSave(CONTENT_1);
        createNoteAndSave(CONTENT_2);
        // when
        webTestClient.get()
                .uri(baseUrl + "/count-all/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(2);
    }

    @Test
    void updatesLastSeenAt() {
        // given
        NoteDto noteDto = createNoteAndSave(CONTENT_1);
        // when
        var lastSeenAt = Timestamp.from(Instant.now());
        webTestClient.patch()
                .uri(baseUrl + "/patch/last-seen-at/")
                .bodyValue(PatchNoteLastSeenAtInput.builder()
                        .lastSeenAt(lastSeenAt)
                        .ids(List.of(noteDto.getId()))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(noteDto.getId())
                .jsonPath("$[0].content").isEqualTo(noteDto.getContent())
                .jsonPath("$[0].lastSeenAt").isNotEmpty();
        // then
        NoteDto responseBody = webTestClient.get()
                .uri(baseUrl + noteDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(NoteDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody.getLastSeenAt()).isCloseTo(lastSeenAt, 0);
    }

    private void assertThatNoteIsNotFoundById(long id) {
        webTestClient.get()
                .uri(baseUrl + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + id)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Note not found by id: " + id);
    }

    private NoteDto createNoteAndSave(String content1) {
        return noteService.add(AddNoteInput.builder()
                .content(content1)
                .build());
    }

    private TagDto createTag(long id, String tagName) {
        return TagDto.builder().id(id).name(tagName).build();
    }

}
