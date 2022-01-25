package tagservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TagServiceApplication

fun main(args: Array<String>) {
    runApplication<TagServiceApplication>(*args)
}
