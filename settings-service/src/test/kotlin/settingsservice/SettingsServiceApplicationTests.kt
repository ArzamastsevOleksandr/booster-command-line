package settingsservice

import api.settings.CreateSettingsInput
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@SpringBootTest(
    classes = [SettingsServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class SettingsServiceApplicationTests {

    @Autowired
    lateinit var settingsService: SettingsService

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun shouldReturn404WhenSettingsNotFound() {
        webTestClient.get()
            .uri("/settings/")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.timestamp").isNotEmpty
            .jsonPath("$.path").isEqualTo("/settings/")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Settings not found")
    }

    @Test
    fun shouldFindSettings() {
        // given
        val settingsDto = settingsService.create(
            CreateSettingsInput.builder()
                .defaultLanguageId(1)
                .entriesPerVocabularyTrainingSession(5)
                .build()
        )
        // then
        webTestClient.get()
            .uri("/settings/")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(settingsDto.id)
            .jsonPath("$.defaultLanguageId").isEqualTo(1)
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(5)
    }

    @Test
    fun shouldCreateSettings() {
        // when
        webTestClient.post()
            .uri("/settings/")
            .bodyValue(CreateSettingsInput(1, 5))
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.defaultLanguageId").isEqualTo(1)
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(5)
        // then
        val settingsDto = settingsService.findOne()
        assertThat(settingsDto?.defaultLanguageId).isEqualTo(1)
        assertThat(settingsDto?.entriesPerVocabularyTrainingSession).isEqualTo(5)
    }

}
