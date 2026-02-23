package monster.monge.follow.application.required

import monster.monge.follow.domain.Follow
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow, Long> {
    fun findByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Follow?
    fun findAllByFollowingId(followingId: Long): List<Follow>
    fun findAllByFollowerId(followerId: Long): List<Follow>
}
