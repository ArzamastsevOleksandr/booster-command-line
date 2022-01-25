package tagservice

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@SpringBootTest(
    classes = [TagServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class TagServiceApplicationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun shouldReturnEmptyCollectionWhenNoTagsFound() {
        webTestClient.get()
            .uri("/tags/")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    fun returns404WhenTagNotFoundById() {
        val id = 1
        webTestClient.get()
            .uri("/tags/$id")
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.timestamp").isNotEmpty
            .jsonPath("$.path").isEqualTo("/tags/$id")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Tag not found by id: $id")
    }

}
