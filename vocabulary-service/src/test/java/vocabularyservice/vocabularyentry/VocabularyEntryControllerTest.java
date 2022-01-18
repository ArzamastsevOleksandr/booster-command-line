package vocabularyservice.vocabularyentry;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class VocabularyEntryControllerTest {

    @Autowired
    VocabularyEntryRepository vocabularyEntryRepository;
    @Autowired
    TestVocabularyEntryService testVocabularyEntryService;
    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnAllVocabularyEntries() {
        // given
        var name = "entry";
        // when
        Long id = testVocabularyEntryService.createVocabularyEntry(name);
        // then
        webTestClient.get()
                .uri("/vocabulary-entries/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(id)
                .jsonPath("$[0].name").isEqualTo(name)
                .jsonPath("$[0].correctAnswersCount").isEqualTo(0)
                .jsonPath("$[0].definition").isEqualTo(name)
                .jsonPath("$[0].synonyms.length()").isEqualTo(1)
                .jsonPath("$[0].synonyms[0]").isEqualTo(name);
    }

}