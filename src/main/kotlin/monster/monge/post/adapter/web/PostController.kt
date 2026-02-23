package monster.monge.post.adapter.web

import monster.monge.global.extension.accountId
import monster.monge.post.application.provided.PostManager
import monster.monge.post.domain.Post
import monster.monge.post.domain.PostCategory
import monster.monge.post.domain.PostComment
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/posts")
class PostController(
    private val postManager: PostManager
) {

    @PostMapping
    fun createPost(
        authentication: JwtAuthenticationToken,
        @RequestBody request: PostCreateRequest
    ): PostResponse {
        val post = postManager.createPost(
            authentication.accountId(),
            request.content,
            request.imageUrl,
            request.weightAtPost,
            request.category
        )
        return PostResponse.from(post)
    }

    @GetMapping("/feed")
    fun getFeed(
        authentication: JwtAuthenticationToken,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): List<PostResponse> {
        return postManager.getFeed(authentication.accountId(), page, size)
            .map { PostResponse.from(it) }
    }

    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): PostResponse {
        return PostResponse.from(postManager.getPost(id))
    }

    @PutMapping("/{id}")
    fun updatePost(
        authentication: JwtAuthenticationToken,
        @PathVariable id: Long,
        @RequestBody request: PostUpdateRequest
    ): PostResponse {
        val post = postManager.updatePost(id, authentication.accountId(), request.content, request.imageUrl)
        return PostResponse.from(post)
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        authentication: JwtAuthenticationToken,
        @PathVariable id: Long
    ) {
        postManager.deletePost(id, authentication.accountId())
    }

    @PostMapping("/{postId}/likes")
    fun likePost(
        authentication: JwtAuthenticationToken,
        @PathVariable postId: Long
    ) {
        postManager.likePost(postId, authentication.accountId())
    }

    @DeleteMapping("/{postId}/likes")
    fun unlikePost(
        authentication: JwtAuthenticationToken,
        @PathVariable postId: Long
    ) {
        postManager.unlikePost(postId, authentication.accountId())
    }

    @PostMapping("/{postId}/comments")
    fun addComment(
        authentication: JwtAuthenticationToken,
        @PathVariable postId: Long,
        @RequestBody request: CommentRequest
    ): CommentResponse {
        val comment = postManager.addComment(postId, authentication.accountId(), request.content)
        return CommentResponse.from(comment)
    }

    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(
        authentication: JwtAuthenticationToken,
        @PathVariable commentId: Long
    ) {
        postManager.deleteComment(commentId, authentication.accountId())
    }
}

data class CommentRequest(val content: String)

data class CommentResponse(
    val id: Long?,
    val postId: Long,
    val accountId: Long,
    val content: String
) {
    companion object {
        fun from(comment: PostComment) = CommentResponse(
            comment.id,
            comment.postId,
            comment.accountId,
            comment.content
        )
    }
}

data class PostCreateRequest(
    val content: String,
    val imageUrl: String?,
    val weightAtPost: BigDecimal?,
    val category: PostCategory
)

data class PostUpdateRequest(
    val content: String,
    val imageUrl: String?
)

data class PostResponse(
    val id: Long?,
    val accountId: Long,
    val content: String,
    val imageUrl: String?,
    val weightAtPost: BigDecimal?,
    val category: PostCategory,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(post: Post) = PostResponse(
            post.id,
            post.accountId,
            post.content,
            post.imageUrl,
            post.weightAtPost,
            post.category,
            post.createdAt
        )
    }
}
