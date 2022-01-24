package settingsservice

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SettingsRepository : JpaRepository<SettingsEntity, Long> {

    fun findFirstBy(): SettingsEntity?

}
