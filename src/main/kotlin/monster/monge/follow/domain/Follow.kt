package monster.monge.follow.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "follows")
class Follow(
    val followerId: Long,
    val followingId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue
    val id: Long? = null,
)
