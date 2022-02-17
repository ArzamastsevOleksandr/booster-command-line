package settingsservice.feign

import api.vocabulary.LanguageControllerApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(value = "language-api-client", url = "http://localhost:8082/languages")
interface LanguageClient : LanguageControllerApi