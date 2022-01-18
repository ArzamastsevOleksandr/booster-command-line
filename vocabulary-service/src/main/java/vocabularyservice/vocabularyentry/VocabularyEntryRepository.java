package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface VocabularyEntryRepository extends JpaRepository<VocabularyEntryEntity, Long> {
}
