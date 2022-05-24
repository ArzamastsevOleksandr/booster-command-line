package booster.settings

import api.settings.CreateSettingsInput
import api.settings.PatchSettingsInput
import api.settings.SettingsDto
import api.settings.SettingsApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/")
class SettingsController : SettingsApi {

    @Autowired
    lateinit var settingsService: SettingsService

    override fun findOne(): SettingsDto {
        return settingsService.findOne()
    }

    override fun create(input: CreateSettingsInput): SettingsDto {
        return settingsService.create(input)
    }

    override fun delete() {
        settingsService.delete()
    }

    override fun patch(input: PatchSettingsInput): SettingsDto {
        return settingsService.patch(input);
    }

}