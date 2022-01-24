package settingsservice

import api.settings.SettingsDto
import api.settings.SettingsServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/")
class SettingsController : SettingsServiceApi {

    @Autowired
    lateinit var settingsService: SettingsService

    override fun findOne(): SettingsDto? {
        return settingsService.findOne()
    }

}