package monster.monge.account.adapter.web.clerk

import monster.monge.account.application.provided.AccountRegister
import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegisterRequest
import monster.monge.global.config.SecurityConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@Import(SecurityConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class ClerkControllerTest {
    @MockitoBean
    private lateinit var signatureValidator: SvixSignatureValidator

    @MockitoBean
    private lateinit var accountRegister: AccountRegister

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mvc: MockMvcTester

    @BeforeEach
    fun setUp(context: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        val mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint())
            )
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
        mvc = MockMvcTester.create(mockMvc)
    }

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

        // when
        `when`(accountRegister.register(request)).thenReturn(Account(email, providerId, 1L))

        // then
        mvc.post()
            .uri("/webhooks/clerk")
            .header("svix-id", "msg_123")
            .header("svix-signature", "v1,abc")
            .header("svix-timestamp", "1234567890")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(clerkPayload))
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("clerk-webhook"))
    }
}
