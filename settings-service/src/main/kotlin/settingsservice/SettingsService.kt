package settingsservice

import api.exception.NotFoundException
import api.settings.CreateSettingsInput
import api.settings.PatchSettingsInput
import api.settings.SettingsDto
import api.vocabulary.LanguageApi
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional.ofNullable

@Slf4j
@Service
@Transactional(readOnly = true)
class SettingsService {

    @Autowired
    lateinit var settingsRepository: SettingsRepository
    @Autowired
    lateinit var languageApi: LanguageApi

    fun findOne(): SettingsDto {
        return settingsRepository.findFirstBy()?.let { toDto(it) }
            ?: throw NotFoundException("Settings not found")
    }

    private fun toDto(settingsEntity: SettingsEntity): SettingsDto {
        return SettingsDto.builder()
            .id(settingsEntity.id)

            .defaultLanguageName(settingsEntity.defaultLanguageName)

            .entriesPerVocabularyTrainingSession(settingsEntity.entriesPerVocabularyTrainingSession)

            .vocabularyPagination(settingsEntity.vocabularyPagination)
            .languagesPagination(settingsEntity.languagesPagination)
            .notesPagination(settingsEntity.notesPagination)
            .tagsPagination(settingsEntity.tagsPagination)

            .build()
    }

    @Transactional
    fun create(input: CreateSettingsInput): SettingsDto {
        val settingsEntity = SettingsEntity()
        // todo: feign exceptions policy
        // todo: test
        input.defaultLanguageName
            ?.let { languageApi.findByLanguageName(it) }
            ?.let { settingsEntity.defaultLanguageName = it }

        settingsEntity.entriesPerVocabularyTrainingSession = input.entriesPerVocabularyTrainingSession

        settingsEntity.notesPagination = input.notesPagination
        settingsEntity.vocabularyPagination = input.vocabularyPagination
        settingsEntity.languagesPagination = input.languagesPagination
        settingsEntity.tagsPagination = input.tagsPagination

        return toDto(settingsRepository.save(settingsEntity))
    }

    @Transactional
    fun delete() {
        val settingsDto = findOne()
        settingsRepository.deleteById(settingsDto.id)
    }

    @Deprecated(message = "Use a standard rfc approach")
    @Transactional
    fun patch(input: PatchSettingsInput): SettingsDto {
        val settingsEntity = settingsRepository.findFirstBy() ?: throw NotFoundException("Settings not found")

        ofNullable(input.entriesPerVocabularyTrainingSession).ifPresent { count ->
            settingsEntity.entriesPerVocabularyTrainingSession = count
        }
        return toDto(settingsRepository.save(settingsEntity))
    }

}
