package monster.monge.profile.infrastructure.persistence

import monster.monge.profile.domain.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface JpaProfileRepository : JpaRepository<Profile, Long> {
    fun findByAccountId(accountId: Long): Profile?
}
