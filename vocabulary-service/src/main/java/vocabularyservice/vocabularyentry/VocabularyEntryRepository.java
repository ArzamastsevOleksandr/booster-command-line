package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vocabularyservice.language.Language;

import java.util.List;
import java.util.Optional;
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

    // todo: prevent null from being set at all
    @Query(value = """
            select distinct(ve.language)
            from VocabularyEntryEntity ve
            where ve.language is not null
    """)
    List<String> myLanguages();

    Stream<VocabularyEntryEntity> findAllByLanguage(Language language);

    Optional<VocabularyEntryEntity> findByWordId(Long id);

    @Query("select new java.lang.Boolean(count(*) > 0) from VocabularyEntryEntity where word.id = :id")
    Boolean existsWithWordId(@Param("id") Long id);

    @Query(value = """
            select *
            from vocabulary_entries
            where id in (select vocabulary_entry_entity_id from vocabulary_entries_translations)
            order by correct_answers_count, last_seen_at
            limit ?1
            """, nativeQuery = true)
    Stream<VocabularyEntryEntity> findWithTranslations(Integer limit);

}
