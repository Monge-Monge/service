package monster.monge.profile.application.required

import monster.monge.profile.domain.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long> {
    fun findByAccountId(accountId: Long): Profile?
}
