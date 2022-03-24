package vocabularyservice.language;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import vocabularyservice.BaseIntegrationTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LanguageControllerTest extends BaseIntegrationTest {

    String baseUrl = "/languages/";

    @Test
    void returnsAllLanguages() {
        webTestClient.get()
                .uri(baseUrl)
                .exchange()
                .expectStatus().isOk()
                // for some reason expectBodyList returns a single result of concatenated languages
                .expectBody(Object[].class)
                .consumeWith(entityExchangeResult -> {
                    List<String> expectedLanguages = Arrays.stream(Language.values())
                            .map(Language::getName)
                            .toList();

                    List<String> actualLanguages = Arrays.stream(entityExchangeResult.getResponseBody())
                            .map(Object::toString)
                            .toList();

                    assertThat(expectedLanguages).containsAll(actualLanguages);
                });
    }

    @Test
    @Disabled("implement when this command is ready")
    void returnsMyLanguages() {
    }

    @Test
    void findsLanguageByName() {
        Arrays.stream(Language.values())
                .map(Language::getName)
                .forEach(language -> webTestClient.get()
                        .uri(baseUrl + "name/" + language)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").isEqualTo(language));
    }

    @Test
    void returns404WhenLanguageByNameNotFound() {
        var randomUUID = UUID.randomUUID();
        webTestClient.get()
                .uri(baseUrl + "name/" + randomUUID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(baseUrl + "name/" + randomUUID)
                .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name())
                .jsonPath("$.message").isEqualTo("No language found by name: " + randomUUID);
    }

}
