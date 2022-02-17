package settingsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["settingsservice", "api"])
@EnableFeignClients
class SettingsServiceApplication

fun main(args: Array<String>) {
    runApplication<SettingsServiceApplication>(*args)
}
