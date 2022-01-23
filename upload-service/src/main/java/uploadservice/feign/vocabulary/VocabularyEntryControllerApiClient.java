package uploadservice.feign.vocabulary;

import api.vocabulary.VocabularyEntryControllerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "vocabulary-entry-api-client", url = "http://localhost:8082/vocabulary-entries")
public interface VocabularyEntryControllerApiClient extends VocabularyEntryControllerApi {
}
