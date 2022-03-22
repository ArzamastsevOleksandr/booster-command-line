package settingsservice

import api.settings.CreateSettingsInput
import api.settings.PatchSettingsInput
import api.vocabulary.LanguageApi
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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

    @MockBean
    lateinit var languageApi: LanguageApi

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
        val defaultLanguageId: Long = 1
        val entriesPerVocabularyTrainingSession = 5
        // given
        val settingsDto =
            settingsService.create(
                CreateSettingsInput.builder()
                    .entriesPerVocabularyTrainingSession(entriesPerVocabularyTrainingSession)
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
            .jsonPath("$.defaultLanguageId").isEqualTo(defaultLanguageId)
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(entriesPerVocabularyTrainingSession)
    }

    @Test
    fun shouldCreateSettings() {
        val entriesPerVocabularyTrainingSession = 5
        // when
        webTestClient.post()
            .uri("/settings/")
            .bodyValue(
                CreateSettingsInput.builder()
                    .entriesPerVocabularyTrainingSession(entriesPerVocabularyTrainingSession)
                    .build()
            )
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(entriesPerVocabularyTrainingSession)
        // then
        val settingsDto = settingsService.findOne()
        assertThat(settingsDto.entriesPerVocabularyTrainingSession).isEqualTo(entriesPerVocabularyTrainingSession)
    }

    @Test
    fun returns404WhenDeletingSettingsThatDoNotExist() {
        webTestClient.delete()
            .uri("/settings/")
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
    fun shouldDeleteSettings() {
        // given
        val settingsDto = settingsService.create(
            CreateSettingsInput.builder()
                .entriesPerVocabularyTrainingSession(5)
                .build()
        )
        // when
        webTestClient.delete()
            .uri("/settings/")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody()
            .isEmpty
        // then
        assertThatThrownBy { settingsService.findOne() }
            .hasMessage("Settings not found")
    }

    @Test
    fun shouldPatchSettings() {
        // given
        val settingsDto = settingsService.create(
            CreateSettingsInput.builder()
                .entriesPerVocabularyTrainingSession(2)
                .build()
        )
        // when
        val factor = 5
        webTestClient.patch()
            .uri("/settings/")
            .bodyValue(
                PatchSettingsInput.builder()
                    .entriesPerVocabularyTrainingSession(settingsDto.entriesPerVocabularyTrainingSession * factor)
                    .build()
            )
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(settingsDto.id)
            .jsonPath("$.entriesPerVocabularyTrainingSession")
            .isEqualTo(settingsDto.entriesPerVocabularyTrainingSession * factor)
        // then
        val patched = settingsService.findOne()
        assertThat(patched.entriesPerVocabularyTrainingSession).isEqualTo(settingsDto.entriesPerVocabularyTrainingSession * factor)
    }

    @Test
    fun returns404WhenPatchCalledAndSettingsDoNotExist() {
        webTestClient.patch()
            .uri("/settings/")
            .bodyValue(PatchSettingsInput(2))
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.timestamp").isNotEmpty
            .jsonPath("$.path").isEqualTo("/settings/")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Settings not found")
    }

}
