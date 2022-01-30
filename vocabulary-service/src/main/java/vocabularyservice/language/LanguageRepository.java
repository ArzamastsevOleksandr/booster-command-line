package vocabularyservice.language;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {

    Optional<LanguageEntity> findByName(String name);

}
