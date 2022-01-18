package vocabularyservice.language;

import api.vocabulary.LanguageDto;
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
class LanguageControllerTest {

    @Autowired
    TestLanguageService testLanguageService;
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

}
