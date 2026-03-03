package monster.monge.post.infrastructure.persistence

import monster.monge.post.domain.*
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val jpaPostRepository: JpaPostRepository
) : PostRepository {
    override fun save(post: Post): Post = jpaPostRepository.save(post)
    override fun delete(post: Post) = jpaPostRepository.delete(post)
    override fun findById(id: Long): Post? = jpaPostRepository.findById(id).orElse(null)
    override fun findAllByAccountIdInOrderByCreatedAtDesc(accountIds: List<Long>, pageable: Pageable): List<Post> = 
        jpaPostRepository.findAllByAccountIdInOrderByCreatedAtDesc(accountIds, pageable)
}

@Repository
class InteractionsPersistenceAdapter(
    private val jpaPostLikeRepository: JpaPostLikeRepository,
    private val jpaPostCommentRepository: JpaPostCommentRepository
) : PostLikeRepository, PostCommentRepository {
    override fun save(postLike: PostLike): PostLike = jpaPostLikeRepository.save(postLike)
    override fun delete(postLike: PostLike) = jpaPostLikeRepository.delete(postLike)
    override fun findByPostIdAndAccountId(postId: Long, accountId: Long): PostLike? = 
        jpaPostLikeRepository.findByPostIdAndAccountId(postId, accountId)
    override fun save(postComment: PostComment): PostComment = jpaPostCommentRepository.save(postComment)
    override fun delete(postComment: PostComment) = jpaPostCommentRepository.delete(postComment)
    override fun findById(id: Long): PostComment? = jpaPostCommentRepository.findById(id).orElse(null)
}
