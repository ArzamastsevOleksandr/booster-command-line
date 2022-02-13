package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {

    @Query(value = """
            select *
            from vocabulary_entries
            order by last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findFirst(Integer limit);

    Stream<VocabularyEntryEntity> findAllByLanguageId(Long id);

    @Query(value = """
            select *
            from vocabulary_entries
            where id in (select vocabulary_entry_entity_id from vocabulary_entries_synonyms)
            order by last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findWithSynonyms(Integer limit);

    Integer countAllBy();

    Integer countAllByWordNameContaining(String substring);

}
