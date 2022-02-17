package settingsservice

import javax.persistence.*

@Entity
@Table(name = "settings")
open class SettingsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,

    open var defaultLanguageId: Long? = null,
    open var defaultLanguageName: String? = null,

    open var entriesPerVocabularyTrainingSession: Int? = 5

)
