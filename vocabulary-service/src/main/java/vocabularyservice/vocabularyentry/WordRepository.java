package vocabularyservice.vocabularyentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface WordRepository extends JpaRepository<WordEntity, Long> {

    Optional<WordEntity> findByName(String name);

}
