package cliclient.service;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryDto;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    public void updateLastSeenAt(VocabularyEntryDto entry) {
        vocabularyEntryControllerApiClient.patchEntry(PatchVocabularyEntryInput.builder()
                .id(entry.getId())
                .lastSeenAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

}
