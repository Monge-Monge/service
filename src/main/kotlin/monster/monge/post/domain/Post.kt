package monster.monge.post.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
class Post(
    val accountId: Long,
    var content: String,
    var imageUrl: String? = null,
    val weightAtPost: BigDecimal? = null,
    @Enumerated(EnumType.STRING)
    val category: PostCategory,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Id @GeneratedValue
    val id: Long? = null,
)

enum class PostCategory {
    DIET, EXERCISE, PROGRESS, FREE
}
