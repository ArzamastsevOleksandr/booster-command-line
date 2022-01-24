package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {

    Stream<VocabularyEntryEntity> findAllByLanguageId(Long id);

}
