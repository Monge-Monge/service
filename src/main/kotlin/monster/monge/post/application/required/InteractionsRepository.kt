package monster.monge.post.application.required

import monster.monge.post.domain.PostComment
import monster.monge.post.domain.PostLike
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository : JpaRepository<PostLike, Long> {
    fun findByPostIdAndAccountId(postId: Long, accountId: Long): PostLike?
}

interface PostCommentRepository : JpaRepository<PostComment, Long>
