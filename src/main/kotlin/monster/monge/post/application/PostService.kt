package monster.monge.post.application

import monster.monge.follow.application.required.FollowRepository
import monster.monge.post.application.provided.PostManager
import monster.monge.post.application.required.PostCommentRepository
import monster.monge.post.application.required.PostLikeRepository
import monster.monge.post.application.required.PostRepository
import monster.monge.post.domain.Post
import monster.monge.post.domain.PostCategory
import monster.monge.post.domain.PostComment
import monster.monge.post.domain.PostLike
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
    private val postLikeRepository: PostLikeRepository,
    private val postCommentRepository: PostCommentRepository
) : PostManager {

    override fun createPost(
        accountId: Long,
        content: String,
        imageUrl: String?,
        weightAtPost: BigDecimal?,
        category: PostCategory
    ): Post {
        val post = Post(accountId, content, imageUrl, weightAtPost, category)
        return postRepository.save(post)
    }

    override fun getPost(id: Long): Post {
        return postRepository.findById(id).orElseThrow { NoSuchElementException("Post not found: $id") }
    }

    override fun updatePost(id: Long, accountId: Long, content: String, imageUrl: String?): Post {
        val post = getPost(id)
        if (post.accountId != accountId) throw IllegalArgumentException("Not your post")
        post.content = content
        post.imageUrl = imageUrl
        return postRepository.save(post)
    }

    override fun deletePost(id: Long, accountId: Long) {
        val post = getPost(id)
        if (post.accountId != accountId) throw IllegalArgumentException("Not your post")
        postRepository.delete(post)
    }

    override fun getFeed(accountId: Long, page: Int, size: Int): List<Post> {
        val followingIds = followRepository.findAllByFollowerId(accountId).map { it.followingId }
        val targetIds = followingIds + accountId
        return postRepository.findAllByAccountIdInOrderByCreatedAtDesc(targetIds, PageRequest.of(page, size))
    }

    override fun likePost(postId: Long, accountId: Long) {
        if (postLikeRepository.findByPostIdAndAccountId(postId, accountId) != null) return
        postLikeRepository.save(PostLike(postId, accountId))
    }

    override fun unlikePost(postId: Long, accountId: Long) {
        postLikeRepository.findByPostIdAndAccountId(postId, accountId)?.let {
            postLikeRepository.delete(it)
        }
    }

    override fun addComment(postId: Long, accountId: Long, content: String): PostComment {
        return postCommentRepository.save(PostComment(postId, accountId, content))
    }

    override fun deleteComment(commentId: Long, accountId: Long) {
        val comment = postCommentRepository.findById(commentId)
            .orElseThrow { NoSuchElementException("Comment not found: $commentId") }
        if (comment.accountId != accountId) throw IllegalArgumentException("Not your comment")
        postCommentRepository.delete(comment)
    }
}
