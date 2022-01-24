package settingsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["settingsservice", "api"])
class SettingsServiceApplication

fun main(args: Array<String>) {
    runApplication<SettingsServiceApplication>(*args)
}
