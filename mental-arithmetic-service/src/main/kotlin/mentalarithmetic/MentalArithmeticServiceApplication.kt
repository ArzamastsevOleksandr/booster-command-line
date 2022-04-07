package mentalarithmetic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["api", "mentalarithmetic"])
class MentalArithmeticServiceApplication

fun main(args: Array<String>) {
    runApplication<MentalArithmeticServiceApplication>(*args)
}
