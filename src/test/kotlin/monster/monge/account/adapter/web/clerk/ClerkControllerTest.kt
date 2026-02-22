package monster.monge.account.adapter.web.clerk

import monster.monge.account.application.provided.AccountRegister
import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegisterRequest
import monster.monge.global.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import tools.jackson.databind.ObjectMapper

@AutoConfigureRestDocs
@WebMvcTest(controllers = [ClerkController::class])
@Import(SecurityConfig::class)
class ClerkControllerTest {
    @MockitoBean
    private lateinit var signatureValidator: SvixSignatureValidator

    @MockitoBean
    private lateinit var accountRegister: AccountRegister

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvcTester

    @Test
    fun `웹훅이 정상적으로 처리된다`() {
        // given
        val email = "test@example.com"
        val providerId = "user_123"
        val request = AccountRegisterRequest(email, providerId)

        val clerkPayload = mapOf(
            "data" to mapOf(
                "email_addresses" to listOf(
                    mapOf("email_address" to email)
                ),
                "id" to providerId
            ),
            "type" to "user.created"
        )
        val payload = objectMapper.writeValueAsString(clerkPayload)
        val svixId = "msg_123"
        val svixSignature = "v1,abc"
        val svixTimestamp = "1234567890"

        // when
        `when`(accountRegister.register(request)).thenReturn(Account(email, providerId, 1L))

        // then
        mvc.post()
            .uri("/webhooks/clerk")
            .header("svix-id", svixId)
            .header("svix-signature", svixSignature)
            .header("svix-timestamp", svixTimestamp)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("clerk-webhook"))
    }
}
