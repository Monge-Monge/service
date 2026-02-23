package monster.monge.profile.adapter.web

import monster.monge.global.extension.accountId
import monster.monge.profile.application.provided.ProfileManager
import monster.monge.profile.domain.Profile
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/profiles")
class ProfileController(
    private val profileManager: ProfileManager
) {

    @GetMapping("/me")
    fun getMyProfile(authentication: JwtAuthenticationToken): ProfileResponse {
        val profile = profileManager.getProfile(authentication.accountId())
        return ProfileResponse.from(profile)
    }

    @PutMapping("/me")
    fun updateMyProfile(
        authentication: JwtAuthenticationToken,
        @RequestBody request: ProfileUpdateRequest
    ): ProfileResponse {
        val profile = profileManager.updateProfile(
            authentication.accountId(),
            request.nickname,
            request.profileImageUrl,
            request.goalWeight,
            request.height,
            request.bio,
            request.isPublic
        )
        return ProfileResponse.from(profile)
    }

    @GetMapping("/{accountId}")
    fun getProfile(@PathVariable accountId: Long): ProfileResponse {
        val profile = profileManager.getProfile(accountId)
        return ProfileResponse.from(profile)
    }
}

data class ProfileResponse(
    val accountId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val goalWeight: BigDecimal?,
    val height: BigDecimal?,
    val bio: String?,
    val isPublic: Boolean
) {
    companion object {
        fun from(profile: Profile) = ProfileResponse(
            profile.accountId,
            profile.nickname,
            profile.profileImageUrl,
            profile.goalWeight,
            profile.height,
            profile.bio,
            profile.isPublic
        )
    }
}

data class ProfileUpdateRequest(
    val nickname: String,
    val profileImageUrl: String?,
    val goalWeight: BigDecimal?,
    val height: BigDecimal?,
    val bio: String?,
    val isPublic: Boolean
)
