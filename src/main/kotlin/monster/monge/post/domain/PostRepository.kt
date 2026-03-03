package monster.monge.post.domain

import org.springframework.data.domain.Pageable

interface PostRepository {
    fun save(post: Post): Post
    fun delete(post: Post)
    fun findById(id: Long): Post?
    fun findAllByAccountIdInOrderByCreatedAtDesc(accountIds: List<Long>, pageable: Pageable): List<Post>
}

interface PostLikeRepository {
    fun save(postLike: PostLike): PostLike
    fun delete(postLike: PostLike)
    fun findByPostIdAndAccountId(postId: Long, accountId: Long): PostLike?
}

interface PostCommentRepository {
    fun save(postComment: PostComment): PostComment
    fun delete(postComment: PostComment)
    fun findById(id: Long): PostComment?
}
