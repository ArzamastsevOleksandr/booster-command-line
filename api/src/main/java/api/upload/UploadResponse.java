package api.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {

    int vocabularyEntriesUploaded;
    int notesUploaded;
    int tagsUploaded;
    // todo: include settings?

    public static UploadResponse empty() {
        return new UploadResponse(0, 0, 0);
    }

}
