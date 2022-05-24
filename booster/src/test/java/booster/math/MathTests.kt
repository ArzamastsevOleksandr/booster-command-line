package booster.math

import api.math.ArithmeticExpression
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD)
class MathTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    val baseUrl = "/mental-arithmetic/"

    @Test
    fun getsArithmeticChallenge() {
        for (i in 1..100) {
            val arithmeticExpression = webTestClient.get()
                .uri("${baseUrl}random/1")
                .exchange()
                .expectStatus().isOk
                .expectBody(ArithmeticExpression::class.java)
                .returnResult()
                .responseBody

//            println("${arithmeticExpression?.a}${arithmeticExpression?.op}${arithmeticExpression?.b}")
            assertThat(arithmeticExpression?.op).isEqualTo("*")
            assertThat(arithmeticExpression?.a).isBetween(11, 20)
            assertThat(arithmeticExpression?.b).isBetween(11, 20)
        }
    }

}
