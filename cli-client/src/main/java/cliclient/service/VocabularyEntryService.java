package cliclient.service;

import api.vocabulary.PatchVocabularyEntryLastSeenAtInput;
import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryApi vocabularyEntryApi;

    public void updateLastSeenAt(Collection<VocabularyEntryDto> vocabularyEntryDtos) {
        vocabularyEntryApi.patchLastSeenAt(PatchVocabularyEntryLastSeenAtInput.builder()
                .ids(vocabularyEntryDtos.stream().map(VocabularyEntryDto::getId).toList())
                .lastSeenAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

}
