package vocabularyservice.language;

import api.exception.NotFoundException;
import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import vocabularyservice.BaseIntegrationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class LanguageControllerTest extends BaseIntegrationTest {

    @Test
    void shouldReturnAllLanguages() {
        // given
        var name1 = "English";
        var name2 = "German";
        // when
        LanguageDto languageDto1 = languageService.add(new AddLanguageInput(name1));
        LanguageDto languageDto2 = languageService.add(new AddLanguageInput(name2));
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

                    assertThat(languageDtos).containsExactlyInAnyOrder(
                            new LanguageDto(languageDto1.id(), languageDto1.name()),
                            new LanguageDto(languageDto2.id(), languageDto2.name())
                    );
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
        var name = "English";
        // when
        LanguageDto languageDto = languageService.add(new AddLanguageInput(name));
        // then
        webTestClient.get()
                .uri("/languages/" + languageDto.id())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(languageDto.id())
                .jsonPath("$.name").isEqualTo(name);
    }

    @Test
    void shouldFindLanguageByName() {
        // given
        var name = "English";
        // when
        LanguageDto languageDto = languageService.add(new AddLanguageInput(name));
        // then
        webTestClient.get()
                .uri("/languages/name/" + languageDto.name())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(languageDto.id())
                .jsonPath("$.name").isEqualTo(languageDto.name());
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
    void shouldReturn404WhenLanguageByNameNotFound() {
        var name = "English";
        webTestClient.get()
                .uri("/languages/name/" + name)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/languages/name/" + name)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("Language not found by name: " + name);
    }

    @Test
    void shouldAddLanguage() {
        // given
        assertThat(languageService.getAll()).isEmpty();
        // when
        var name = "English";
        webTestClient.post()
                .uri("/languages/")
                .bodyValue(new AddLanguageInput(name))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(LanguageDto.class)
                .consumeWith(response -> {
                    LanguageDto responseDto = response.getResponseBody();

                    assertThat(responseDto.id()).isNotNull();
                    assertThat(responseDto.name()).isEqualTo(name);

                    List<LanguageDto> languageDtos = new ArrayList<>(languageService.getAll());
                    assertThat(languageDtos).hasSize(1);

                    LanguageDto languageDto = languageDtos.get(0);

                    assertThat(languageDto.id()).isEqualTo(responseDto.id());
                    assertThat(languageDto.name()).isEqualTo(name);
                });
    }

    @Test
    void shouldDeleteLanguageById() {
        // given
        var name = "English";
        LanguageDto languageDto = languageService.add(new AddLanguageInput(name));
        // when
        webTestClient.delete()
                .uri("/languages/" + languageDto.id())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        // then
        assertThatThrownBy(() -> languageService.findById(languageDto.id()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Language not found by id: " + languageDto.id());
    }

}
