package monster.monge.follow.domain

interface FollowRepository {
    fun save(follow: Follow): Follow
    fun delete(follow: Follow)
    fun findByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Follow?
    fun findAllByFollowingId(followingId: Long): List<Follow>
    fun findAllByFollowerId(followerId: Long): List<Follow>
}
