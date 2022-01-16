package notesservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface NoteRepository extends JpaRepository<NoteEntity, UUID> {
}
