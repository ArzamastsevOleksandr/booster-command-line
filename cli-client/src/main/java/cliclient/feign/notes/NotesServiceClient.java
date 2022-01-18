package cliclient.feign.notes;

import api.notes.NoteServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "notes-service-client", url = "http://localhost:8081/notes/")
public interface NotesServiceClient extends NoteServiceApi {
}
