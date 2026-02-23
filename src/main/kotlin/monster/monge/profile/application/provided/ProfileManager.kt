package monster.monge.profile.application.provided

import monster.monge.profile.domain.Profile
import java.math.BigDecimal

interface ProfileManager {
    fun getProfile(accountId: Long): Profile
    fun updateProfile(
        accountId: Long,
        nickname: String,
        profileImageUrl: String?,
        goalWeight: BigDecimal?,
        height: BigDecimal?,
        bio: String?,
        isPublic: Boolean
    ): Profile
}
