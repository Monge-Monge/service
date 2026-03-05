package monster.monge.profile.domain

interface ProfileRepository {
    fun save(profile: Profile): Profile
    fun findByAccountId(accountId: Long): Profile?
    fun findById(id: Long): Profile?
}
