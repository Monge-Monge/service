package monster.monge.weight.adapter.web

import monster.monge.account.application.required.AccountRepository
import monster.monge.global.config.SecurityConfig
import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.provided.WeightRecorder
import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(WeightController::class)
@Import(SecurityConfig::class)
@ExtendWith(RestDocumentationExtension::class)
class WeightControllerTest(
    @MockitoBean private val weightRecorder: WeightRecorder,
    @MockitoBean private val weightFinder: WeightFinder,
    @MockitoBean private val accountRepository: AccountRepository,
) {

    private lateinit var mockMvc: MockMvc
    private lateinit var mvcTester: MockMvcTester

    @BeforeEach
    fun setup(context: WebApplicationContext, restDocs: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocs).uris().withScheme("https").withHost("api.monge.monster"))
            .build()
        mvcTester = MockMvcTester.create(mockMvc)
    }

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("accountId", accountId) }

    @Test
    fun `POST api weights 는 몸무게를 생성한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo", 1L)
        `when`(
            weightRecorder.record(
                accountId,
                BigDecimal("75.5"),
                LocalDate.of(2025, 1, 1),
                "memo"
            )
        ).thenReturn(weight)

        mockMvc.perform(
            post("/weights")
                .with(jwtAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":75.5,"recordedAt":"2025-01-01","memo":"memo"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andDo(document("weights-create", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `GET api weights 는 목록을 반환한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 2), null, 2L),
            Weight(accountId, BigDecimal("76.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightFinder.findAll(accountId)).thenReturn(weights)

        mockMvc.perform(get("/weights").with(jwtAuth()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andDo(document("weights-list", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `GET api weights id 는 단건을 반환한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightFinder.findById(accountId, 1L)).thenReturn(weight)

        mockMvc.perform(get("/weights/{id}", 1).with(jwtAuth()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andDo(document("weights-get", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `PUT api weights id 는 수정한다`() {
        val updated = Weight(accountId, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new", 1L)
        `when`(weightRecorder.update(accountId, 1L, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new")).thenReturn(
            updated
        )

        mockMvc.perform(
            put("/weights/{id}", 1)
                .with(jwtAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":74.0,"recordedAt":"2025-01-02","memo":"new"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.value").value(74.0))
            .andDo(document("weights-update", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `DELETE api weights id 는 삭제한다`() {
        mockMvc.perform(delete("/weights/{id}", 1).with(jwtAuth()))
            .andExpect(status().isNoContent)
            .andDo(document("weights-delete", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))

        verify(weightRecorder).delete(accountId, 1L)
    }

    @Test
    fun `GET api weights graph 는 그래프 데이터를 반환한다`() {
        val weights = listOf(Weight(accountId, BigDecimal("75.0"), LocalDate.of(2025, 1, 1), null, 1L))
        `when`(weightFinder.graph(accountId, "WEEK")).thenReturn(weights)

        mockMvc.perform(get("/weights/graph").param("period", "WEEK").with(jwtAuth()))
            .andExpect(status().isOk)
            .andDo(document("weights-graph", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `GET api weights stats 는 통계를 반환한다`() {
        val stat = WeightStat(BigDecimal("78.0"), BigDecimal("74.0"), BigDecimal("76.00"), BigDecimal("4.0"))
        `when`(weightFinder.stats(accountId)).thenReturn(stat)

        mockMvc.perform(get("/weights/stats").with(jwtAuth()))
            .andExpect(status().isOk)
            .andDo(document("weights-stats", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
    }

    @Test
    fun `인증 없이 접근하면 401을 반환한다`() {
        mockMvc.perform(get("/weights"))
            .andExpect(status().isUnauthorized)
            .andDo(
                document(
                    "weights-unauthorized",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                )
            )
    }
}
