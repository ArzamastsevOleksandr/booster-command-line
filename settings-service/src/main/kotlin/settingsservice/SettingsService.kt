package settingsservice

import api.exception.NotFoundException
import api.settings.CreateSettingsInput
import api.settings.PatchSettingsInput
import api.settings.SettingsDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional.ofNullable

@Slf4j
@Service
@Transactional
class SettingsService {

    @Autowired
    lateinit var settingsRepository: SettingsRepository

    @Transactional(readOnly = true)
    fun findOne(): SettingsDto {
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

    fun create(input: CreateSettingsInput): SettingsDto {
        val settingsEntity = SettingsEntity()
        settingsEntity.defaultLanguageId = input.defaultLanguageId
        settingsEntity.entriesPerVocabularyTrainingSession = input.entriesPerVocabularyTrainingSession
        return toDto(settingsRepository.save(settingsEntity))
    }

    fun delete() {
        val settingsDto = findOne()
        settingsRepository.deleteById(settingsDto.id)
    }

    fun patch(input: PatchSettingsInput): SettingsDto {
        val settingsEntity = settingsRepository.findFirstBy() ?: throw NotFoundException("Settings not found")

        ofNullable(input.defaultLanguageId).ifPresent { id -> settingsEntity.defaultLanguageId = id }
        ofNullable(input.entriesPerVocabularyTrainingSession).ifPresent { count ->
            settingsEntity.entriesPerVocabularyTrainingSession = count
        }
        return toDto(settingsRepository.save(settingsEntity))
    }

}
