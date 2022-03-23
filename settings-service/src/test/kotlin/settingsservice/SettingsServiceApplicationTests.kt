package settingsservice

import api.settings.CreateSettingsInput
import api.settings.PatchSettingsInput
import api.settings.SettingsDto
import api.vocabulary.LanguageApi
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
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
    fun returns404WhenSettingsNotFound() {
        assertThatSettingsNotFound()
    }

    @Test
    fun findsSettings() {
        // given
        val settingsDto = createSettings()
        given(languageApi.findByLanguageName(settingsDto.defaultLanguageName)).willReturn(settingsDto.defaultLanguageName)
        // when
        webTestClient.get()
            .uri("/settings/")
            .exchange()
            // then
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(settingsDto.id)
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(settingsDto.entriesPerVocabularyTrainingSession)
            .jsonPath("$.defaultLanguageName").isEqualTo(settingsDto.defaultLanguageName)
            .jsonPath("$.notesPagination").isEqualTo(settingsDto.notesPagination)
            .jsonPath("$.tagsPagination").isEqualTo(settingsDto.tagsPagination)
            .jsonPath("$.languagesPagination").isEqualTo(settingsDto.languagesPagination)
            .jsonPath("$.vocabularyPagination").isEqualTo(settingsDto.vocabularyPagination)
    }

    @Test
    fun createsSettings() {
        // given
        val input = createSettingsInput()
        given(languageApi.findByLanguageName(input.defaultLanguageName)).willReturn(input.defaultLanguageName)
        // when
        webTestClient.post()
            .uri("/settings/")
            .bodyValue(input)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(input.entriesPerVocabularyTrainingSession)
            .jsonPath("$.defaultLanguageName").isEqualTo(input.defaultLanguageName)
            .jsonPath("$.notesPagination").isEqualTo(input.notesPagination)
            .jsonPath("$.tagsPagination").isEqualTo(input.tagsPagination)
            .jsonPath("$.languagesPagination").isEqualTo(input.languagesPagination)
            .jsonPath("$.vocabularyPagination").isEqualTo(input.vocabularyPagination)
        // then
        webTestClient.get()
            .uri("/settings/")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.entriesPerVocabularyTrainingSession").isEqualTo(input.entriesPerVocabularyTrainingSession)
            .jsonPath("$.defaultLanguageName").isEqualTo(input.defaultLanguageName)
            .jsonPath("$.notesPagination").isEqualTo(input.notesPagination)
            .jsonPath("$.tagsPagination").isEqualTo(input.tagsPagination)
            .jsonPath("$.languagesPagination").isEqualTo(input.languagesPagination)
            .jsonPath("$.vocabularyPagination").isEqualTo(input.vocabularyPagination)
    }

    @Test
    fun returns404WhenDeletingSettingsThatDoNotExist() {
        webTestClient.delete()
            .uri("/settings/")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("/settings/")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Settings not found")
    }

    @Test
    fun deletesSettings() {
        // given
        createSettings()
        // when
        webTestClient.delete()
            .uri("/settings/")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody()
            .isEmpty
        // then
        assertThatSettingsNotFound()
    }

    @Test
    @Disabled("Finish me when patch is used from cli-client")
    fun shouldPatchSettings() {
    }

    @Test
    @Disabled("Finish me when patch is used from cli-client")
    fun returns404WhenPatchCalledAndSettingsDoNotExist() {
        webTestClient.patch()
            .uri("/settings/")
            .bodyValue(PatchSettingsInput(2))
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("/settings/")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Settings not found")
    }

    private fun createSettingsInput() = CreateSettingsInput.builder()
        .entriesPerVocabularyTrainingSession(2)

        .defaultLanguageName("ENG")

        .notesPagination(2)
        .tagsPagination(2)
        .languagesPagination(2)
        .vocabularyPagination(2)

        .build()

    private fun createSettings(): SettingsDto {
        val input = createSettingsInput()
        return settingsService.create(input)
    }

    private fun assertThatSettingsNotFound() {
        webTestClient.get()
            .uri("/settings/")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("/settings/")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Settings not found")
    }

}
