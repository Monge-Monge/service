package monster.monge.follow.application.provided

import monster.monge.profile.domain.Profile

interface FollowManager {
    fun follow(followerId: Long, followingId: Long)
    fun unfollow(followerId: Long, followingId: Long)
    fun getFollowers(accountId: Long): List<Profile>
    fun getFollowings(accountId: Long): List<Profile>
}
