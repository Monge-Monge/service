package monster.monge.post.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "post_likes")
class PostLike(
    val postId: Long,
    val accountId: Long,
    @Id @GeneratedValue
    val id: Long? = null,
)

@Entity
@Table(name = "post_comments")
class PostComment(
    val postId: Long,
    val accountId: Long,
    val content: String,
    @Id @GeneratedValue
    val id: Long? = null,
)
