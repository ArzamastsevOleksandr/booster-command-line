package tagservice

import javax.persistence.*

@Entity
@Table(name = "tags")
open class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null

    open lateinit var name: String

}
