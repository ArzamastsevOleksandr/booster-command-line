package cliclient.service;

import api.vocabulary.PatchVocabularyEntryLastSeenAtInput;
import api.vocabulary.VocabularyEntryDto;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryControllerApiClient vocabularyEntryClient;

    public void updateLastSeenAt(Collection<VocabularyEntryDto> vocabularyEntryDtos) {
        vocabularyEntryClient.patchLastSeenAt(PatchVocabularyEntryLastSeenAtInput.builder()
                .ids(vocabularyEntryDtos.stream().map(VocabularyEntryDto::getId).toList())
                .lastSeenAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

}
