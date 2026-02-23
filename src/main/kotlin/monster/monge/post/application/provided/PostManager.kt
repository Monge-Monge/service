package monster.monge.post.application.provided

import monster.monge.post.domain.Post
import monster.monge.post.domain.PostCategory
import monster.monge.post.domain.PostComment
import java.math.BigDecimal

interface PostManager {
    fun createPost(
        accountId: Long,
        content: String,
        imageUrl: String?,
        weightAtPost: BigDecimal?,
        category: PostCategory
    ): Post

    fun getPost(id: Long): Post
    fun updatePost(id: Long, accountId: Long, content: String, imageUrl: String?): Post
    fun deletePost(id: Long, accountId: Long)
    fun getFeed(accountId: Long, page: Int, size: Int): List<Post>

    fun likePost(postId: Long, accountId: Long)
    fun unlikePost(postId: Long, accountId: Long)
    fun addComment(postId: Long, accountId: Long, content: String): PostComment
    fun deleteComment(commentId: Long, accountId: Long)
}
