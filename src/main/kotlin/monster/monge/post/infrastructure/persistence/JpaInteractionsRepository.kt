package monster.monge.post.infrastructure.persistence

import monster.monge.post.domain.PostComment
import monster.monge.post.domain.PostLike
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPostLikeRepository : JpaRepository<PostLike, Long> {
    fun findByPostIdAndAccountId(postId: Long, accountId: Long): PostLike?
}

interface JpaPostCommentRepository : JpaRepository<PostComment, Long>
