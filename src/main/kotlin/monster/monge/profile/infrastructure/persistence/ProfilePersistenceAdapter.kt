package monster.monge.profile.infrastructure.persistence

import monster.monge.profile.domain.Profile
import monster.monge.profile.domain.ProfileRepository
import org.springframework.stereotype.Repository

@Repository
class ProfilePersistenceAdapter(
    private val jpaProfileRepository: JpaProfileRepository
) : ProfileRepository {
    override fun save(profile: Profile): Profile = jpaProfileRepository.save(profile)
    override fun findByAccountId(accountId: Long): Profile? = jpaProfileRepository.findByAccountId(accountId)
    override fun findById(id: Long): Profile? = jpaProfileRepository.findById(id).orElse(null)
}
