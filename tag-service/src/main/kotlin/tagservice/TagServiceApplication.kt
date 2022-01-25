package tagservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["tagservice", "api"])
class TagServiceApplication

fun main(args: Array<String>) {
    runApplication<TagServiceApplication>(*args)
}
