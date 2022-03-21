package vocabularyservice.vocabularyentry;

import api.exception.NotFoundException;
import api.vocabulary.*;
import net.minidev.json.JSONArray;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vocabularyservice.BaseIntegrationTest;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class VocabularyEntryControllerControllerTest extends BaseIntegrationTest {

    @Autowired
    VocabularyEntryService vocabularyEntryService;

    @Test
    void shouldReturnFirstVocabularyEntries() {
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
                .uri("/vocabulary-entries/?limit=1")
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
    void shouldFindAllVocabularyEntriesByLanguageId() {
        // given
        var name1 = "entry1";
        var name2 = "entry2";
        var definition1 = "walk with effort1";
        var definition2 = "walk with effort2";
        var synonyms1 = Set.of("ford1", "paddle1");
        var synonyms2 = Set.of("ford2", "paddle2");

        var english = "English";
        var german = "German";

        LanguageDto languageDto1 = languageService.add(new AddLanguageInput(english));
        LanguageDto languageDto2 = languageService.add(new AddLanguageInput(german));

        VocabularyEntryDto vocabularyEntryDto1 = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name1)
                .languageId(languageDto1.id())
                .definition(definition1)
                .synonyms(synonyms1)
                .build());
        VocabularyEntryDto vocabularyEntryDto2 = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name2)
                .languageId(languageDto2.id())
                .definition(definition2)
                .synonyms(synonyms2)
                .build());

        // then
        webTestClient.get()
                .uri("/vocabulary-entries/languageId/" + languageDto1.id())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntryDto1.getId())
                .jsonPath("$[0].name").isEqualTo(vocabularyEntryDto1.getName())
                .jsonPath("$[0].correctAnswersCount").isEqualTo(vocabularyEntryDto1.getCorrectAnswersCount())
                .jsonPath("$[0].definition").isEqualTo(vocabularyEntryDto1.getDefinition())
                .jsonPath("$[0].lastSeenAt").isEqualTo(vocabularyEntryDto1.getLastSeenAt().getTime())
                .jsonPath("$[0].synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms1);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms1);
                    }
                })
                .jsonPath("$[0].language.id").isEqualTo(languageDto1.id())
                .jsonPath("$[0].language.name").isEqualTo(languageDto1.name());

        webTestClient.get()
                .uri("/vocabulary-entries/languageId/" + languageDto2.id())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntryDto2.getId())
                .jsonPath("$[0].name").isEqualTo(vocabularyEntryDto2.getName())
                .jsonPath("$[0].correctAnswersCount").isEqualTo(vocabularyEntryDto2.getCorrectAnswersCount())
                .jsonPath("$[0].definition").isEqualTo(vocabularyEntryDto2.getDefinition())
                .jsonPath("$[0].lastSeenAt").isEqualTo(vocabularyEntryDto2.getLastSeenAt().getTime())
                .jsonPath("$[0].synonyms").value(new BaseMatcher<HashSet<String>>() {
                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Expected a set containing all of: " + synonyms2);
                    }

                    @Override
                    public boolean matches(Object actual) {
                        JSONArray jsonArray = (JSONArray) actual;
                        return new HashSet<>(jsonArray).equals(synonyms2);
                    }
                })
                .jsonPath("$[0].language.id").isEqualTo(languageDto2.id())
                .jsonPath("$[0].language.name").isEqualTo(languageDto2.name());
    }

    @Test
    void shouldCreateVocabularyEntry() {
        // given
        assertThat(vocabularyEntryService.findFirst(10)).isEmpty();
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

    // todo: a test for ?limit=n
    @Test
    void findsVocabularyEntriesWithSynonyms() {
        // given
        var name1 = "wade";
        var definition1 = "walk with effort";
        var synonyms1 = Set.of("ford", "paddle");

        LanguageDto languageDto = languageService.add(new AddLanguageInput("English"));
        VocabularyEntryDto vocabularyEntryDto1 = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name1)
                .languageId(languageDto.id())
                .definition(definition1)
                .synonyms(synonyms1)
                .build());

        var name2 = "fallacy";
        var definition2 = "a mistaken belief";
        VocabularyEntryDto vocabularyEntryDto2 = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                .name(name2)
                .definition(definition2)
                .languageId(languageDto.id())
                .build());
        // when
        webTestClient.get()
                .uri("/vocabulary-entries/with-synonyms/?limit=2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntryDto1.getId());
    }

}