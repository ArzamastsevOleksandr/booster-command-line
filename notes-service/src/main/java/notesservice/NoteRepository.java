package notesservice;

import org.springframework.data.jpa.repository.JpaRepository;

interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    Integer countAllBy();

}
