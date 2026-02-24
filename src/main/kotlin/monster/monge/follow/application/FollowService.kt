package monster.monge.follow.application

import monster.monge.follow.application.provided.FollowManager
import monster.monge.follow.application.required.FollowRepository
import monster.monge.follow.domain.Follow
import monster.monge.profile.application.required.ProfileRepository
import monster.monge.profile.domain.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FollowService(
    private val followRepository: FollowRepository,
    private val profileRepository: ProfileRepository
) : FollowManager {

    override fun follow(followerId: Long, followingId: Long) {
        if (followerId == followingId) throw IllegalArgumentException("Cannot follow yourself")
        if (followRepository.findByFollowerIdAndFollowingId(followerId, followingId) != null) return

        followRepository.save(Follow(followerId, followingId))
    }

    override fun unfollow(followerId: Long, followingId: Long) {
        followRepository.findByFollowerIdAndFollowingId(followerId, followingId)?.let {
            followRepository.delete(it)
        }
    }

    override fun getFollowers(accountId: Long): List<Profile> {
        val followerIds = followRepository.findAllByFollowingId(accountId).map { it.followerId }
        return followerIds.mapNotNull { profileRepository.findByAccountId(it) }
    }

    override fun getFollowings(accountId: Long): List<Profile> {
        val followingIds = followRepository.findAllByFollowerId(accountId).map { it.followingId }
        return followingIds.mapNotNull { profileRepository.findByAccountId(it) }
    }
}
