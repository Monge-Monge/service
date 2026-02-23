package monster.monge.follow.adapter.web

import monster.monge.account.application.required.AccountRepository
import monster.monge.global.config.SecurityConfig
import monster.monge.follow.application.provided.FollowManager
import monster.monge.profile.domain.Profile
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester

@AutoConfigureRestDocs
@WebMvcTest(controllers = [FollowController::class])
@Import(SecurityConfig::class)
class FollowControllerTest {

    @MockitoBean
    private lateinit var followManager: FollowManager

    @MockitoBean
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var mvc: MockMvcTester

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("sub_id", accountId) }

    @Test
    fun `POST api follows accountId 는 팔로우한다`() {
        val followingId = 2L

        mvc.post()
            .uri("/follows/{accountId}", followingId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("follows-create"))

        verify(followManager).follow(accountId, followingId)
    }

    @Test
    fun `DELETE api follows accountId 는 언팔로우한다`() {
        val followingId = 2L

        mvc.delete()
            .uri("/follows/{accountId}", followingId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("follows-delete"))

        verify(followManager).unfollow(accountId, followingId)
    }

    @Test
    fun `GET api follows followers 는 팔로워 목록을 반환한다`() {
        val followers = listOf(Profile(2L, "follower", null))
        `when`(followManager.getFollowers(accountId)).thenReturn(followers)

        mvc.get()
            .uri("/follows/followers")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("follows-followers-list"))
            .hasBodyTextEqualTo("""[{"accountId":2,"nickname":"follower","profileImageUrl":null,"goalWeight":null,"height":null,"bio":null,"isPublic":false}]""")
    }

    @Test
    fun `GET api follows followings 는 팔로잉 목록을 반환한다`() {
        val followings = listOf(Profile(3L, "following", null))
        `when`(followManager.getFollowings(accountId)).thenReturn(followings)

        mvc.get()
            .uri("/follows/followings")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("follows-followings-list"))
            .hasBodyTextEqualTo("""[{"accountId":3,"nickname":"following","profileImageUrl":null,"goalWeight":null,"height":null,"bio":null,"isPublic":false}]""")
    }
}
