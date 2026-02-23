package monster.monge.profile.adapter.web

import monster.monge.account.application.required.AccountRepository
import monster.monge.global.config.SecurityConfig
import monster.monge.profile.application.provided.ProfileManager
import monster.monge.profile.domain.Profile
import org.junit.jupiter.api.Test
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

@AutoConfigureRestDocs
@WebMvcTest(controllers = [ProfileController::class])
@Import(SecurityConfig::class)
class ProfileControllerTest {

    @MockitoBean
    private lateinit var profileManager: ProfileManager

    @MockitoBean
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var mvc: MockMvcTester

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("sub_id", accountId) }

    @Test
    fun `GET api profiles me 는 내 프로필을 반환한다`() {
        val profile = Profile(accountId, "nickname", "url", BigDecimal("70.0"), BigDecimal("170.0"), "bio", true, 1L)
        `when`(profileManager.getProfile(accountId)).thenReturn(profile)

        mvc.get()
            .uri("/profiles/me")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("profiles-me-get"))
            .hasBodyTextEqualTo("""{"accountId":1,"nickname":"nickname","profileImageUrl":"url","goalWeight":70.0,"height":170.0,"bio":"bio","isPublic":true}""")
    }

    @Test
    fun `PUT api profiles me 는 프로필을 수정한다`() {
        val updated = Profile(accountId, "newNick", "newUrl", BigDecimal("65.0"), BigDecimal("170.0"), "newBio", false, 1L)
        `when`(profileManager.updateProfile(accountId, "newNick", "newUrl", BigDecimal("65.0"), BigDecimal("170.0"), "newBio", false))
            .thenReturn(updated)

        mvc.put()
            .uri("/profiles/me")
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "nickname": "newNick",
                    "profileImageUrl": "newUrl",
                    "goalWeight": 65.0,
                    "height": 170.0,
                    "bio": "newBio",
                    "isPublic": false
                }
            """.trimIndent())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("profiles-me-update"))
            .hasBodyTextEqualTo("""{"accountId":1,"nickname":"newNick","profileImageUrl":"newUrl","goalWeight":65.0,"height":170.0,"bio":"newBio","isPublic":false}""")
    }

    @Test
    fun `GET api profiles accountId 는 다른 유저의 프로필을 반환한다`() {
        val otherAccountId = 2L
        val profile = Profile(otherAccountId, "other", "url", null, null, "hello", true, 2L)
        `when`(profileManager.getProfile(otherAccountId)).thenReturn(profile)

        mvc.get()
            .uri("/profiles/{accountId}", otherAccountId)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("profiles-get"))
            .hasBodyTextEqualTo("""{"accountId":2,"nickname":"other","profileImageUrl":"url","goalWeight":null,"height":null,"bio":"hello","isPublic":true}""")
    }
}
