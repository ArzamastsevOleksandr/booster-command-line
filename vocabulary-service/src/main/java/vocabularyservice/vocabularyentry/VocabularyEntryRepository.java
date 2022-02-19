package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {

    @Query(value = """
            select *
            from vocabulary_entries ve
            order by ve.last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findFirst(Integer limit);

    @Query(value = """
            select *
            from vocabulary_entries ve
            inner join words w
            on w.id = ve.word_id
            where w.name like ?2
            order by ve.last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findFirstWithSubstring(Integer limit, String substring);

    Stream<VocabularyEntryEntity> findAllByLanguageId(Long id);

    @Query(value = """
            select *
            from vocabulary_entries
            where id in (select vocabulary_entry_entity_id from vocabulary_entries_synonyms)
            order by correct_answers_count, last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findWithSynonyms(Integer limit);

    Integer countAllBy();

    Integer countAllByWordNameContaining(String substring);

}
