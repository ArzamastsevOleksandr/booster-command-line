package settingsservice

import api.exception.NotFoundException
import api.settings.CreateSettingsInput
import api.settings.SettingsDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional(readOnly = true)
class SettingsService {

    @Autowired
    lateinit var settingsRepository: SettingsRepository

    fun findOne(): SettingsDto? {
        return settingsRepository.findFirstBy()?.let { toDto(it) }
            ?: throw NotFoundException("Settings not found")
    }

    private fun toDto(settingsEntity: SettingsEntity): SettingsDto {
        return SettingsDto(
            settingsEntity.id,
            settingsEntity.defaultLanguageId,
            settingsEntity.entriesPerVocabularyTrainingSession
        )
    }

    @Transactional
    fun create(input: CreateSettingsInput): SettingsDto {
        val settingsEntity = SettingsEntity()
        settingsEntity.defaultLanguageId = input.defaultLanguageId
        settingsEntity.entriesPerVocabularyTrainingSession = input.entriesPerVocabularyTrainingSession
        return toDto(settingsRepository.save(settingsEntity))
    }

}
