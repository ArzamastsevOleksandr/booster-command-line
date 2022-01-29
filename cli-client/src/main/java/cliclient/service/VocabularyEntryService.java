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

    private static final int MIN_CORRECT_ANSWERS_COUNT = 0;

    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    public void incCorrectAnswersCount(VocabularyEntryDto entry) {
        updateCorrectAnswersCount(entry, true);
    }

    public void decCorrectAnswersCount(VocabularyEntryDto entry) {
        updateCorrectAnswersCount(entry, false);
    }

    private void updateCorrectAnswersCount(VocabularyEntryDto entry, boolean correct) {
        int change = correct ? 1 : -1;
        int newValue = entry.getCorrectAnswersCount() + change;
        if (isValidCorrectAnswersCount(newValue)) {
            vocabularyEntryControllerApiClient.patchEntry(PatchVocabularyEntryInput.builder()
                    .id(entry.getId())
                    .correctAnswersCount(newValue)
                    .build());
        }
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated;
    }

    public void updateLastSeenAt(VocabularyEntryDto entry) {
        vocabularyEntryControllerApiClient.patchEntry(PatchVocabularyEntryInput.builder()
                .id(entry.getId())
                .lastSeenAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

}
