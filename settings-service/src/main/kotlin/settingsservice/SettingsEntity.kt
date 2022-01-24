package settingsservice

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SettingsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    val defaultLanguageId: Long,

    val entriesPerVocabularyTrainingSession: Int = 5

)
