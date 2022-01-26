package notesservice;

import org.springframework.data.jpa.repository.JpaRepository;

interface TagIdRepository extends JpaRepository<TagIdEntity, Long> {
}
