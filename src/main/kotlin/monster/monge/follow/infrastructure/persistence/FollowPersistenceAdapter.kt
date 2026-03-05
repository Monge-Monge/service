package monster.monge.follow.infrastructure.persistence

import monster.monge.follow.domain.Follow
import monster.monge.follow.domain.FollowRepository
import org.springframework.stereotype.Repository

@Repository
class FollowPersistenceAdapter(
    private val jpaFollowRepository: JpaFollowRepository
) : FollowRepository {
    override fun save(follow: Follow): Follow = jpaFollowRepository.save(follow)
    override fun delete(follow: Follow) = jpaFollowRepository.delete(follow)
    override fun findByFollowerIdAndFollowingId(followerId: Long, followingId: Long): Follow? = 
        jpaFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId)
    override fun findAllByFollowingId(followingId: Long): List<Follow> = 
        jpaFollowRepository.findAllByFollowingId(followingId)
    override fun findAllByFollowerId(followerId: Long): List<Follow> = 
        jpaFollowRepository.findAllByFollowerId(followerId)
}
