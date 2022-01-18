package vocabularyservice.language;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class LanguageControllerTest {

    @Autowired
    TestLanguageService testLanguageService;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnAllLanguages() {
        // given
        var name1 = "ENGLISH";
        var name2 = "GERMAN";
        // when
        Long id1 = testLanguageService.createLanguage(name1);
        Long id2 = testLanguageService.createLanguage(name2);
        // then
        webTestClient.get()
                .uri("/languages/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Object[].class)
                .consumeWith(response -> {
                    List<LanguageDto> languageDtos = Arrays.stream(response.getResponseBody())
                            .map(obj -> new ObjectMapper().convertValue(obj, LanguageDto.class))
                            .toList();

                    assertThat(languageDtos).containsExactlyInAnyOrder(new LanguageDto(id1, name1), new LanguageDto(id2, name2));
                });
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoLanguages() {
        webTestClient.get()
                .uri("/languages/")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldFindLanguageById() {
        // given
        var name = "ENGLISH";
        // when
        Long id = testLanguageService.createLanguage(name);
        // then
        webTestClient.get()
                .uri("/languages/" + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.name").isEqualTo(name);
    }

    @Test
    void shouldReturn404WhenLanguageByIdNotFound() {
        long id = 1000;
        webTestClient.get()
                .uri("/languages/" + id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/languages/" + id)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Language not found by id: " + id);
    }

    @Test
    void shouldAddLanguage() {
        // given
        assertThat(languageRepository.findAll()).isEmpty();
        // when
        var name = "ENGLISH";
        webTestClient.post()
                .uri("/languages/")
                .bodyValue(new AddLanguageInput(name))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(LanguageDto.class)
                .consumeWith(response -> {
                    LanguageDto languageDto = response.getResponseBody();

                    assertThat(languageDto.id()).isNotNull();
                    assertThat(languageDto.name()).isEqualTo(name);

                    List<LanguageEntity> languageEntities = languageRepository.findAll();
                    assertThat(languageEntities).hasSize(1);

                    LanguageEntity languageEntity = languageEntities.get(0);
                    assertThat(languageEntity.getId()).isEqualTo(languageDto.id());
                    assertThat(languageEntity.getName()).isEqualTo(name);
                });
    }

}
