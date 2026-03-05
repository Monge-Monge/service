package monster.monge.post.adapter.web

import monster.monge.account.domain.AccountRepository
import monster.monge.global.config.SecurityConfig
import monster.monge.post.application.provided.PostManager
import monster.monge.post.domain.Post
import monster.monge.post.domain.PostCategory
import monster.monge.post.domain.PostComment
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.math.BigDecimal
import java.time.LocalDateTime

@AutoConfigureRestDocs
@WebMvcTest(controllers = [PostController::class])
@Import(SecurityConfig::class)
class PostControllerTest {

    @MockitoBean
    private lateinit var postManager: PostManager

    @MockitoBean
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var mvc: MockMvcTester

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("sub_id", accountId) }

    @Test
    fun `POST api posts 는 게시글을 작성한다`() {
        val post = Post(accountId, "content", "url", BigDecimal("70.0"), PostCategory.DIET, LocalDateTime.of(2025, 2, 23, 10, 0), 1L)
        `when`(postManager.createPost(accountId, "content", "url", BigDecimal("70.0"), PostCategory.DIET)).thenReturn(post)

        mvc.post()
            .uri("/posts")
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "content": "content",
                    "imageUrl": "url",
                    "weightAtPost": 70.0,
                    "category": "DIET"
                }
            """.trimIndent())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-create"))
            .hasBodyTextEqualTo("""{"id":1,"accountId":1,"content":"content","imageUrl":"url","weightAtPost":70.0,"category":"DIET","createdAt":"2025-02-23T10:00:00"}""")
    }

    @Test
    fun `GET api posts feed 는 피드를 조회한다`() {
        val post = Post(accountId, "content", "url", null, PostCategory.FREE, LocalDateTime.of(2025, 2, 23, 11, 0), 1L)
        `when`(postManager.getFeed(accountId, 0, 10)).thenReturn(listOf(post))

        mvc.get()
            .uri("/posts/feed?page=0&size=10")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-feed"))
            .hasBodyTextEqualTo("""[{"id":1,"accountId":1,"content":"content","imageUrl":"url","weightAtPost":null,"category":"FREE","createdAt":"2025-02-23T11:00:00"}]""")
    }

    @Test
    fun `POST api posts postId likes 는 좋아요를 한다`() {
        val postId = 1L
        mvc.post()
            .uri("/posts/{postId}/likes", postId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-like"))

        verify(postManager).likePost(postId, accountId)
    }

    @Test
    fun `DELETE api posts postId likes 는 좋아요를 취소한다`() {
        val postId = 1L
        mvc.delete()
            .uri("/posts/{postId}/likes", postId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-unlike"))

        verify(postManager).unlikePost(postId, accountId)
    }

    @Test
    fun `POST api posts postId comments 는 댓글을 작성한다`() {
        val postId = 1L
        val comment = PostComment(postId, accountId, "comment content", 1L)
        `when`(postManager.addComment(postId, accountId, "comment content")).thenReturn(comment)

        mvc.post()
            .uri("/posts/{postId}/comments", postId)
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"content": "comment content"}""")
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-comment-create"))
            .hasBodyTextEqualTo("""{"id":1,"postId":1,"accountId":1,"content":"comment content"}""")
    }

    @Test
    fun `DELETE api posts comments commentId 는 댓글을 삭제한다`() {
        val commentId = 1L
        mvc.delete()
            .uri("/posts/comments/{commentId}", commentId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("posts-comment-delete"))

        verify(postManager).deleteComment(commentId, accountId)
    }
}
