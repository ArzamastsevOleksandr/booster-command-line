package tagservice

import api.tags.CreateTagInput
import api.tags.TagDto
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
    lateinit var tagService: TagService
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
    fun shouldReturnAllTags() {
        val name1 = "education"
        val name2 = "movies"
        // given
        val tagDto1 = tagService.create(CreateTagInput(name1))
        val tagDto2 = tagService.create(CreateTagInput(name2))
        // when
        val tagDtos = webTestClient.get()
            .uri("/tags/")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(TagDto::class.java)
            .returnResult()
            .responseBody
        // then
        assertThat(tagDtos).hasSize(2)
        assertThat(tagDtos).containsExactlyInAnyOrder(tagDto1, tagDto2)
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

    @Test
    fun findsTagById() {
        val name = "education"
        val tagDto = tagService.create(CreateTagInput(name))

        webTestClient.get()
            .uri("/tags/${tagDto.id}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(tagDto.id)
            .jsonPath("$.name").isEqualTo(tagDto.name)
    }

    @Test
    fun shouldCreateTag() {
        val name = "education"
        // when
        val tagDto = webTestClient.post()
            .uri("/tags/")
            .bodyValue(CreateTagInput(name))
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(TagDto::class.java)
            .returnResult()
            .responseBody

        assertThat(tagDto?.id).isNotNull
        assertThat(tagDto?.name).isEqualTo(name)
        // then
        val tagDtoById = tagService.findById(tagDto?.id ?: throw AssertionError("Tag id must not be null"))
        assertThat(tagDtoById.name).isEqualTo(name)
    }

}
