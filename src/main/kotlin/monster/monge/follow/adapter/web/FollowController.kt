package monster.monge.follow.adapter.web

import monster.monge.follow.application.provided.FollowManager
import monster.monge.global.extension.accountId
import monster.monge.profile.adapter.web.ProfileResponse
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/follows")
class FollowController(
    private val followManager: FollowManager
) {

    @PostMapping("/{accountId}")
    fun follow(
        authentication: JwtAuthenticationToken,
        @PathVariable accountId: Long
    ) {
        followManager.follow(authentication.accountId(), accountId)
    }

    @DeleteMapping("/{accountId}")
    fun unfollow(
        authentication: JwtAuthenticationToken,
        @PathVariable accountId: Long
    ) {
        followManager.unfollow(authentication.accountId(), accountId)
    }

    @GetMapping("/followers")
    fun getFollowers(authentication: JwtAuthenticationToken): List<ProfileResponse> {
        return followManager.getFollowers(authentication.accountId())
            .map { ProfileResponse.from(it) }
    }

    @GetMapping("/followings")
    fun getFollowings(authentication: JwtAuthenticationToken): List<ProfileResponse> {
        return followManager.getFollowings(authentication.accountId())
            .map { ProfileResponse.from(it) }
    }
}
