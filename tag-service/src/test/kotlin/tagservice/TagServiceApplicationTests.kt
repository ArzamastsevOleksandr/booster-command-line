package tagservice

import api.tags.CreateTagInput
import api.tags.TagDto
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import lombok.Data
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

private const val EDUCATION = "education"

private const val MOVIES = "movies"

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

    var baseUrl: String = "/tags/"

    @Test
    fun returnsEmptyCollectionWhenNoTagsFound() {
        webTestClient.get()
            .uri(baseUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    fun returnsAllTags() {
        // given
        val tagDto1 = tagService.create(CreateTagInput(EDUCATION))
        val tagDto2 = tagService.create(CreateTagInput(MOVIES))
        // when
        val tagDtos = webTestClient.get()
            .uri(baseUrl)
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
            .uri("$baseUrl/$id")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("/tags/$id")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Tag not found by id: $id")
    }

    @Test
    fun findsTagById() {
        val tagDto = tagService.create(CreateTagInput(EDUCATION))

        webTestClient.get()
            .uri("$baseUrl/${tagDto.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(tagDto.id)
            .jsonPath("$.name").isEqualTo(tagDto.name)
    }

    @Test
    fun returns404WhenTagNotFoundByName() {
        webTestClient.get()
            .uri("${baseUrl}name/$EDUCATION")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("${baseUrl}name/$EDUCATION")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Tag not found by name: $EDUCATION")
    }

    @Test
    fun findsTagByName() {
        val tagDto = tagService.create(CreateTagInput(EDUCATION))

        webTestClient.get()
            .uri("$baseUrl/name/${tagDto.name}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(tagDto.id)
            .jsonPath("$.name").isEqualTo(tagDto.name)
    }

    @Test
    fun createsTag() {
        // when
        val jsonPathValueRecorder = JsonPathValueRecorder<Int>()
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(CreateTagInput(EDUCATION))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").value<Int> { i -> jsonPathValueRecorder.id = i }
            .jsonPath("$.name").isEqualTo(EDUCATION)
        // then
        webTestClient.get()
            .uri("$baseUrl/${jsonPathValueRecorder.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo(EDUCATION)
            .jsonPath("$.id").isEqualTo(jsonPathValueRecorder.id!!.toInt())
    }

    @Test
    fun returns409WhenCreatingDuplicateTag() {
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(CreateTagInput(EDUCATION))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.name").isEqualTo(EDUCATION)

        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(CreateTagInput(EDUCATION))
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.path").isEqualTo(baseUrl)
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.CONFLICT.name)
            .jsonPath("$.message").isEqualTo("Tag already exists with name: $EDUCATION")
    }

    @Test
    fun findsTagsByNamesInBatch() {
        val tagDto1 = tagService.create(CreateTagInput(EDUCATION))
        val tagDto2 = tagService.create(CreateTagInput(MOVIES))

        val tagDtosByNames = webTestClient.post()
            .uri("${baseUrl}find/name/batch")
            .bodyValue(listOf(EDUCATION, MOVIES))
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TagDto>()
            .returnResult()
            .responseBody

        assertThat(tagDtosByNames).containsExactlyInAnyOrder(tagDto1, tagDto2)
    }

    @Test
    fun returns404WhenDoesNotFindTagsByNamesInBatch() {
        tagService.create(CreateTagInput(EDUCATION))

        webTestClient.post()
            .uri("${baseUrl}find/name/batch")
            .bodyValue(listOf(EDUCATION, MOVIES))
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.path").isEqualTo("${baseUrl}find/name/batch")
            .jsonPath("$.httpStatus").isEqualTo(HttpStatus.NOT_FOUND.name)
            .jsonPath("$.message").isEqualTo("Tags not found: [$MOVIES]")
    }

    @Data
    private class JsonPathValueRecorder<T> {
        var id: T? = null
        var time: Long? = null
    }

}
