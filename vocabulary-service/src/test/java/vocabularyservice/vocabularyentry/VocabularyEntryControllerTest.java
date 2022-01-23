package vocabularyservice.vocabularyentry;

import api.exception.NotFoundException;
import api.vocabulary.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import net.minidev.json.JSONArray;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import vocabularyservice.language.LanguageService;
import vocabularyservice.language.TestLanguageService;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    TestLanguageService testLanguageService;
    @Autowired
    TestWordService testWordService;
    @Autowired
    VocabularyEntryService vocabularyEntryService;
    @Autowired
    LanguageService languageService;
    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnAllVocabularyEntries() {
        // given
        var name = "entry";
        var definition = "walk with effort";
        var synonyms = Set.of("ford", "paddle");
        var english = "English";

        LanguageDto languageDto = languageService.add(new AddLanguageInput(english));
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name)
                .languageId(languageDto.id())
                .definition(definition)
                .synonyms(synonyms)
                .build());
        // then
        webTestClient.get()
                .uri("/vocabulary-entries/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntryDto.getId())
                .jsonPath("$[0].name").isEqualTo(name)
                .jsonPath("$[0].correctAnswersCount").isEqualTo(0)
                .jsonPath("$[0].definition").isEqualTo(definition)
                .jsonPath("$[0].lastSeenAt").isEqualTo(vocabularyEntryDto.getLastSeenAt().getTime())
                .jsonPath("$[0].synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms);
                    }
                })
                .jsonPath("$[0].language.id").isEqualTo(languageDto.id())
                .jsonPath("$[0].language.name").isEqualTo(english);
    }

    @Test
    void shouldCreateVocabularyEntry() {
        // given
        assertThat(vocabularyEntryRepository.findAll()).isEmpty();
        var english = "English";
        LanguageDto languageDto = languageService.add(new AddLanguageInput(english));
        // when
        var name = "wade";
        var definition = "walk with effort";
        var synonyms = Set.of("ford", "paddle");

        webTestClient.post()
                .uri("/vocabulary-entries/")
                .accept(APPLICATION_JSON)
                .bodyValue(AddVocabularyEntryInput.builder()
                        .name(name)
                        .languageId(languageDto.id())
                        .definition(definition)
                        .synonyms(synonyms)
                        .build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.definition").isEqualTo(definition)
                .jsonPath("$.correctAnswersCount").isEqualTo(0)
                .jsonPath("$.lastSeenAt").isNotEmpty()
                .jsonPath("$.synonyms.length()").isEqualTo(synonyms.size())
                .jsonPath("$.language.id").isEqualTo(languageDto.id())
                .jsonPath("$.language.name").isEqualTo(english)
                .jsonPath("$.synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms);
                    }
                });
    }

    // todo: test add ve fails with no languageId

    @Test
    void shouldFindVocabularyEntryById() {
        // given
        var name = "wade";
        var definition = "walk with effort";
        var synonyms = Set.of(name);
        var english = "English";

        LanguageDto languageDto = languageService.add(new AddLanguageInput(english));
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name)
                .languageId(languageDto.id())
                .definition(definition)
                .synonyms(synonyms)
                .build());
        // when + then
        webTestClient.get()
                .uri("/vocabulary-entries/" + vocabularyEntryDto.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(vocabularyEntryDto.getId())
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.definition").isEqualTo(definition)
                .jsonPath("$.correctAnswersCount").isEqualTo(0)
                .jsonPath("$.lastSeenAt").isEqualTo(vocabularyEntryDto.getLastSeenAt().getTime())
                .jsonPath("$.synonyms.length()").isEqualTo(synonyms.size())
                .jsonPath("$.language.id").isEqualTo(languageDto.id())
                .jsonPath("$.language.name").isEqualTo(english)
                .jsonPath("$.synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms);
                    }
                });
    }

    // todo: use real services for test data setup in every test. Use default access levels whenever possible.
    @Test
    void shouldDeleteVocabularyEntryById() {
        // given
        LanguageDto languageDto = languageService.add(new AddLanguageInput("English"));
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name("wade")
                .languageId(languageDto.id())
                .definition("walk with effort")
                .synonyms(Set.of("ford", "paddle"))
                .build());
        // when
        webTestClient.delete()
                .uri("/vocabulary-entries/" + vocabularyEntryDto.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        // then
        assertThatThrownBy(() -> vocabularyEntryService.findById(vocabularyEntryDto.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Vocabulary entry not found by id: " + vocabularyEntryDto.getId());
    }

    // todo: test delete of the entry that does not exist


    @Test
    void shouldPatchById() {
        // given
        var name = "wade";
        var definition = "walk with effort";
        var synonyms = Set.of("ford", "paddle");
        int correctAnswersCount = 10;

        LanguageDto languageDto = languageService.add(new AddLanguageInput("English"));
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name("wad")
                .languageId(languageDto.id())
                .definition("wal wit effor")
                .synonyms(synonyms)
                .build());
        // when
        var lastSeenAt = new Timestamp(System.currentTimeMillis());
        webTestClient.patch()
                .uri("/vocabulary-entries/")
                .bodyValue(PatchVocabularyEntryInput.builder()
                        .id(vocabularyEntryDto.getId())
                        .name(name)
                        .definition(definition)
                        .correctAnswersCount(correctAnswersCount)
                        .lastSeenAt(lastSeenAt)
                        .build())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(vocabularyEntryDto.getId())
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.definition").isEqualTo(definition)
                .jsonPath("$.correctAnswersCount").isEqualTo(correctAnswersCount)
                .jsonPath("$.synonyms.length()").isEqualTo(synonyms.size())
                .jsonPath("$.lastSeenAt").isEqualTo(lastSeenAt.getTime())
                .jsonPath("$.synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms);
                    }
                });
        // then
        VocabularyEntryDto patchedEntry = vocabularyEntryService.findById(vocabularyEntryDto.getId());
        assertAll(
                () -> assertThat(patchedEntry.getName()).isEqualTo(name),
                () -> assertThat(patchedEntry.getDefinition()).isEqualTo(definition),
                () -> assertThat(patchedEntry.getCorrectAnswersCount()).isEqualTo(correctAnswersCount),
                () -> assertThat(patchedEntry.getLastSeenAt()).isEqualTo(lastSeenAt)
        );
    }

}