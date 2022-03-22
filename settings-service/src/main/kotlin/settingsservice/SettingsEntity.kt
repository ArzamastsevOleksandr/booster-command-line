package settingsservice

import javax.persistence.*

@Entity
@Table(name = "settings")
open class SettingsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,

    open var defaultLanguageName: String? = null,

    open var entriesPerVocabularyTrainingSession: Int? = null,

    open var vocabularyPagination: Int? = null,
    open var notesPagination: Int? = null,
    open var languagesPagination: Int? = null,
    open var tagsPagination: Int? = null

)
