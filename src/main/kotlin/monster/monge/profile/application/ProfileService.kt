package monster.monge.profile.application

import monster.monge.profile.application.provided.ProfileManager
import monster.monge.profile.application.required.ProfileRepository
import monster.monge.profile.domain.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class ProfileService(
    private val profileRepository: ProfileRepository
) : ProfileManager {

    override fun getProfile(accountId: Long): Profile {
        return profileRepository.findByAccountId(accountId)
            ?: throw NoSuchElementException("Profile not found for account: $accountId")
    }

    override fun updateProfile(
        accountId: Long,
        nickname: String,
        profileImageUrl: String?,
        goalWeight: BigDecimal?,
        height: BigDecimal?,
        bio: String?,
        isPublic: Boolean
    ): Profile {
        val profile = profileRepository.findByAccountId(accountId)
            ?: Profile(accountId = accountId, nickname = nickname)

        profile.nickname = nickname
        profile.profileImageUrl = profileImageUrl
        profile.goalWeight = goalWeight
        profile.height = height
        profile.bio = bio
        profile.isPublic = isPublic

        return profileRepository.save(profile)
    }
}
