package vocabularyservice.vocabularyentry;

import api.exception.HttpErrorResponse;
import api.settings.SettingsApi;
import api.settings.SettingsDto;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryLastSeenAtInput;
import api.vocabulary.VocabularyEntryDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.Data;
import net.minidev.json.JSONArray;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import vocabularyservice.BaseIntegrationTest;
import vocabularyservice.language.Language;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

class VocabularyEntryControllerControllerTest extends BaseIntegrationTest {

    @Autowired
    VocabularyEntryService vocabularyEntryService;

    @Mock
    FeignException.NotFound notFound;
    @MockBean
    SettingsApi settingsApi;

    String baseUrl = "/vocabulary-entries/";

    @Test
    void findsVocabularyEntryById() {
        // when
        VocabularyEntryDto vocabularyEntryDto = createVocabularyEntry();
        // then
        assertThatVocabularyEntryIsFoundById(vocabularyEntryDto);
    }

    @Test
    void returns404WhenVocabularyEntryNotFoundById() {
        assertThatVocabularyEntryIsNotFoundById(Long.MAX_VALUE);
    }

    @Test
    void deletesVocabularyEntryById() {
        // given
        VocabularyEntryDto vocabularyEntryDto = createVocabularyEntry();
        assertThatVocabularyEntryIsFoundById(vocabularyEntryDto);
        // when
        webTestClient.delete()
                .uri(baseUrl + vocabularyEntryDto.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
        // then
        assertThatVocabularyEntryIsNotFoundById(vocabularyEntryDto.getId());
    }

    @Test
    void returns404WhenDeletingVocabularyEntryThatDoesNotExist() {
        webTestClient.delete()
                .uri(baseUrl + Long.MAX_VALUE)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + Long.MAX_VALUE)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Vocabulary entry not found by id: " + Long.MAX_VALUE);
    }

    @Test
    void createsVocabularyEntry() {
        // given
        AddVocabularyEntryInput input = addVocabularyEntryInput();
        // when
        var jsonPathValueRecorder = new JsonPathValueRecorder<Integer>();
        webTestClient.post()
                .uri(baseUrl)
                .bodyValue(input)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").value(id -> jsonPathValueRecorder.setId((Integer) id))
                .jsonPath("$.name").isEqualTo(input.getName())
                .jsonPath("$.definition").isEqualTo(input.getDefinition())
                .jsonPath("$.correctAnswersCount").isEqualTo(0)
                .jsonPath("$.lastSeenAt").value(lastSeenAt -> jsonPathValueRecorder.setTime((Long) lastSeenAt))
                .jsonPath("$.language").isEqualTo(input.getLanguage())
                .jsonPath("$.synonyms").value(setBaseMatcher(input.getSynonyms()))
                .jsonPath("$.translations").value(setBaseMatcher(input.getTranslations()));
        // then
        var vocabularyEntryDto = new VocabularyEntryDto();
        BeanUtils.copyProperties(input, vocabularyEntryDto);
        vocabularyEntryDto.setId(jsonPathValueRecorder.getId().longValue());
        vocabularyEntryDto.setLastSeenAt(new Timestamp(jsonPathValueRecorder.getTime()));

        assertThatVocabularyEntryIsFoundById(vocabularyEntryDto);
    }

    @Test
    void returns404WhenCreatingVocabularyEntryForLanguageThatDoesNotExist() {
        var language = UUID.randomUUID().toString();

        webTestClient.post()
                .uri(baseUrl)
                .bodyValue(AddVocabularyEntryInput.builder().language(language).build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("No language found by name: " + language.toUpperCase());
    }

    @Test
    void returns404WhenCreatingVocabularyEntryAndNoLanguageSpecifiedAndNoSettingsExist() throws JsonProcessingException {
        // given
        when(notFound.status()).thenReturn(HttpStatus.NOT_FOUND.value());
        var httpErrorResponse = HttpErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .path(baseUrl)
                .build();
        when(notFound.contentUTF8()).thenReturn(new ObjectMapper().writeValueAsString(httpErrorResponse));
        when(settingsApi.findOne()).thenThrow(notFound);
        // then
        webTestClient.post()
                .uri(baseUrl)
                .bodyValue(new AddVocabularyEntryInput())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Language not specified and custom settings do not exist");
    }

    @Test
    void returns404WhenCreatingVocabularyEntryAndNoLanguageSpecifiedAndSettingsDoNotHaveDefaultLanguage() {
        // given
        when(settingsApi.findOne()).thenReturn(new SettingsDto());
        // then
        webTestClient.post()
                .uri(baseUrl)
                .bodyValue(new AddVocabularyEntryInput())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Language not specified and settings do not have a default language");
    }

    @Test
    void createsVocabularyEntryWithDefaultLanguageNameFromSettings() {
        // given
        String language = Language.ENGLISH.getName();
        when(settingsApi.findOne()).thenReturn(SettingsDto.builder().defaultLanguageName(language).build());

        AddVocabularyEntryInput input = addVocabularyEntryInput();
        input.setLanguage(null);
        // when
        var jsonPathValueRecorder = new JsonPathValueRecorder<Integer>();
        webTestClient.post()
                .uri(baseUrl)
                .bodyValue(input)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").value(id -> jsonPathValueRecorder.setId((Integer) id))
                .jsonPath("$.name").isEqualTo(input.getName())
                .jsonPath("$.definition").isEqualTo(input.getDefinition())
                .jsonPath("$.correctAnswersCount").isEqualTo(0)
                .jsonPath("$.lastSeenAt").value(lastSeenAt -> jsonPathValueRecorder.setTime((Long) lastSeenAt))
                .jsonPath("$.language").isEqualTo(language)
                .jsonPath("$.synonyms").value(setBaseMatcher(input.getSynonyms()))
                .jsonPath("$.translations").value(setBaseMatcher(input.getTranslations()));
        // then
        var vocabularyEntryDto = new VocabularyEntryDto();
        BeanUtils.copyProperties(input, vocabularyEntryDto);
        vocabularyEntryDto.setId(jsonPathValueRecorder.getId().longValue());
        vocabularyEntryDto.setLastSeenAt(new Timestamp(jsonPathValueRecorder.getTime()));
        vocabularyEntryDto.setLanguage(language);

        assertThatVocabularyEntryIsFoundById(vocabularyEntryDto);
    }

    @Test
    void countsVocabularyEntries() {
        // given
        webTestClient.get()
                .uri(baseUrl + "/count-all/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(0);
        // when
        createVocabularyEntry();
        // then
        webTestClient.get()
                .uri(baseUrl + "/count-all/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
    }

    @Test
    void countsVocabularyEntriesWithSubstring() {
        // given
        AddVocabularyEntryInput input1 = addVocabularyEntryInput();

        webTestClient.get()
                .uri(baseUrl + "/count-with-substring/" + input1.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(0);
        // when
        createVocabularyEntry(input1);
        // then
        webTestClient.get()
                .uri(baseUrl + "/count-with-substring/" + input1.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
        // when
        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        createVocabularyEntry(input2);
        // then
        webTestClient.get()
                .uri(baseUrl + "/count-with-substring/" + input1.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(2);

        webTestClient.get()
                .uri(baseUrl + "/count-with-substring/" + input2.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
    }

    @Test
    void findsAllByLanguage() {
        // given
        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        webTestClient.get()
                .uri(baseUrl + "/language/" + input1.getLanguage())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
        // when
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);
        // then
        webTestClient.get()
                .uri(baseUrl + "/language/" + input1.getLanguage())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry1.getId());
        // when
        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        VocabularyEntryDto vocabularyEntry2 = createVocabularyEntry(input2);
        // then
        webTestClient.get()
                .uri(baseUrl + "/language/" + input1.getLanguage())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VocabularyEntryDto.class)
                .contains(vocabularyEntry1, vocabularyEntry2);
    }

    @Test
    void patchesLastSeenAtByIds() {
        // given
        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);

        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        VocabularyEntryDto vocabularyEntry2 = createVocabularyEntry(input2);

        AddVocabularyEntryInput input3 = addVocabularyEntryInput(3);
        VocabularyEntryDto vocabularyEntry3 = createVocabularyEntry(input3);
        // when
        var lastSeenAt = new Timestamp(System.currentTimeMillis());

        webTestClient.patch()
                .uri(baseUrl + "/patch/last-seen-at/")
                .bodyValue(PatchVocabularyEntryLastSeenAtInput.builder()
                        .ids(List.of(vocabularyEntry1.getId(), vocabularyEntry2.getId()))
                        .lastSeenAt(lastSeenAt)
                        .build())
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].lastSeenAt").isEqualTo(lastSeenAt.getTime())
                .jsonPath("$[1].lastSeenAt").isEqualTo(lastSeenAt.getTime());

        webTestClient.get()
                .uri(baseUrl + "/language/" + input1.getLanguage())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VocabularyEntryDto.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<VocabularyEntryDto> responseBody = listEntityExchangeResult.getResponseBody();

                    VocabularyEntryDto vocabularyEntryDto1 = responseBody.stream().filter(dto -> vocabularyEntry1.getId().equals(dto.getId())).findFirst().get();
                    assertThat(vocabularyEntryDto1.getLastSeenAt()).isEqualTo(lastSeenAt);

                    VocabularyEntryDto vocabularyEntryDto2 = responseBody.stream().filter(dto -> vocabularyEntry2.getId().equals(dto.getId())).findFirst().get();
                    assertThat(vocabularyEntryDto2.getLastSeenAt()).isEqualTo(lastSeenAt);

                    VocabularyEntryDto vocabularyEntryDto3 = responseBody.stream().filter(dto -> vocabularyEntry3.getId().equals(dto.getId())).findFirst().get();
                    assertThat(vocabularyEntryDto3.getLastSeenAt()).isEqualTo(vocabularyEntry3.getLastSeenAt());
                });
    }

    @Test
    void findsVocabularyEntriesWithSynonyms() {
        // given
        webTestClient.get()
                .uri(baseUrl + "/with-synonyms/?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
        // when
        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);

        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        input2.setSynonyms(Set.of());
        createVocabularyEntry(input2);
        // then
        webTestClient.get()
                .uri(baseUrl + "/with-synonyms/?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry1.getId());
    }

    @Test
    void findsVocabularyEntriesWithTranslations() {
        // given
        webTestClient.get()
                .uri(baseUrl + "/with-translations/?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
        // when
        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);
        // then
        webTestClient.get()
                .uri(baseUrl + "/with-synonyms/?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry1.getId());
        // when
        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        input2.setTranslations(Set.of());
        createVocabularyEntry(input2);
        // then
        webTestClient.get()
                .uri(baseUrl + "/with-synonyms/?limit=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry1.getId());
    }

    @Test
    @Disabled("such tests are not for lazy people")
    void findsVocabularyEntriesWithLimitBasedOnCorrectAnswersCountAndLastSeenAt() {
    }

    @Test
    void returnsVocabularyEntriesSortedByLastSeenAtAscending() {
        // given
        long time = System.currentTimeMillis();

        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        input1.setLastSeenAt(new Timestamp(time));
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);

        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        input2.setLastSeenAt(new Timestamp(time - 1000));
        VocabularyEntryDto vocabularyEntry2 = createVocabularyEntry(input2);

        AddVocabularyEntryInput input3 = addVocabularyEntryInput(3);
        input3.setLastSeenAt(new Timestamp(time - 10_000));
        VocabularyEntryDto vocabularyEntry3 = createVocabularyEntry(input3);
        // when
        webTestClient.get()
                .uri(baseUrl + "?limit=3")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry3.getId())
                .jsonPath("$[1].id").isEqualTo(vocabularyEntry2.getId())
                .jsonPath("$[2].id").isEqualTo(vocabularyEntry1.getId());
    }

    @Test
    void returnsVocabularyEntriesWithSubstringSortedByLastSeenAtAscending() {
        // given
        long time = System.currentTimeMillis();

        AddVocabularyEntryInput input1 = addVocabularyEntryInput();
        input1.setLastSeenAt(new Timestamp(time));
        VocabularyEntryDto vocabularyEntry1 = createVocabularyEntry(input1);

        AddVocabularyEntryInput input2 = addVocabularyEntryInput(2);
        input2.setLastSeenAt(new Timestamp(time - 1000));
        VocabularyEntryDto vocabularyEntry2 = createVocabularyEntry(input2);

        AddVocabularyEntryInput input3 = addVocabularyEntryInput(3);
        input3.setLastSeenAt(new Timestamp(time - 10_000));
        VocabularyEntryDto vocabularyEntry3 = createVocabularyEntry(input3);
        // when
        webTestClient.get()
                .uri(baseUrl + "/with-substring/" + input1.getName() + "?limit=3")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry3.getId())
                .jsonPath("$[1].id").isEqualTo(vocabularyEntry2.getId())
                .jsonPath("$[2].id").isEqualTo(vocabularyEntry1.getId());
        // when
        webTestClient.get()
                .uri(baseUrl + "/with-substring/" + input2.getName() + "?limit=3")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry3.getId())
                .jsonPath("$[1].id").isEqualTo(vocabularyEntry2.getId());
        // when
        webTestClient.get()
                .uri(baseUrl + "/with-substring/" + input3.getName() + "?limit=3")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(vocabularyEntry3.getId());
    }

    @Test
    @Disabled("implement when patch/update policy is clear")
    void patchesById() {
    }

    private void assertThatVocabularyEntryIsNotFoundById(Long id) {
        webTestClient.get()
                .uri(baseUrl + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + id)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Vocabulary entry not found by id: " + id);
    }

    private void assertThatVocabularyEntryIsFoundById(VocabularyEntryDto vocabularyEntryDto) {
        webTestClient.get()
                .uri(baseUrl + vocabularyEntryDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(vocabularyEntryDto.getId())
                .jsonPath("$.name").isEqualTo(vocabularyEntryDto.getName())
                .jsonPath("$.definition").isEqualTo(vocabularyEntryDto.getDefinition())
                .jsonPath("$.correctAnswersCount").isEqualTo(vocabularyEntryDto.getCorrectAnswersCount())
                .jsonPath("$.lastSeenAt").isEqualTo(vocabularyEntryDto.getLastSeenAt().getTime())
                .jsonPath("$.language").isEqualTo(vocabularyEntryDto.getLanguage())
                .jsonPath("$.synonyms").value(setBaseMatcher(vocabularyEntryDto.getSynonyms()))
                .jsonPath("$.translations").value(setBaseMatcher(vocabularyEntryDto.getTranslations()));
    }

    private BaseMatcher<HashSet<String>> setBaseMatcher(Set<String> synonyms) {
        return new BaseMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Expected a set containing all of: " + synonyms);
            }

            @Override
            public boolean matches(Object actual) {
                JSONArray jsonArray = (JSONArray) actual;
                return new HashSet<>(jsonArray).equals(synonyms);
            }
        };
    }

    private VocabularyEntryDto createVocabularyEntry() {
        return createVocabularyEntry(addVocabularyEntryInput());
    }

    private VocabularyEntryDto createVocabularyEntry(AddVocabularyEntryInput input) {
        return vocabularyEntryService.create(input);
    }

    private AddVocabularyEntryInput addVocabularyEntryInput() {
        return addVocabularyEntryInput(1);
    }

    private AddVocabularyEntryInput addVocabularyEntryInput(Integer nameReplication) {
        var input = AddVocabularyEntryInput.builder()
                .definition("of average quality")
                .synonyms(Set.of("ordinary"))
                .language(Language.ENGLISH.getName())
                .translations(Set.of("translations"))
                .build();

        String name = IntStream.range(0, nameReplication).mapToObj(i -> "mediocre").collect(joining("_"));
        input.setName(name);
        return input;
    }

    @Data
    private static class JsonPathValueRecorder<T> {
        T id;
        Long time;
    }

}