package booster.tags

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<TagEntity, Long> {

    fun findByName(name: String): TagEntity?

    @Query("""
        select *
        from tags
        where name in (?1)
    """, nativeQuery = true)
    fun findByNames(names: MutableSet<String>): MutableList<TagEntity>

}
