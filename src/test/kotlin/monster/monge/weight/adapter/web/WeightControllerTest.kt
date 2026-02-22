package monster.monge.weight.adapter.web

import monster.monge.account.application.required.AccountRepository
import monster.monge.global.config.SecurityConfig
import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.provided.WeightRecorder
import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.time.LocalDate

@AutoConfigureRestDocs
@WebMvcTest(controllers = [WeightController::class])
@Import(SecurityConfig::class)
class WeightControllerTest {
    @MockitoBean
    private lateinit var weightRecorder: WeightRecorder

    @MockitoBean
    private lateinit var weightFinder: WeightFinder

    @MockitoBean
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var mvc: MockMvcTester

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("sub_id", accountId) }

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

        mvc.post()
            .uri("/weights")
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"value":75.5,"recordedAt":"2025-01-01","memo":"memo"}""")
            .exchange()
            .assertThat()
            .hasStatus(HttpStatus.CREATED)
            .apply(document("weights-create"))
    }

    @Test
    fun `GET api weights 는 목록을 반환한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 2), null, 2L),
            Weight(accountId, BigDecimal("76.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightFinder.findAll(accountId)).thenReturn(weights)

        mvc.get()
            .uri("/weights")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("weights-list"))
    }

    @Test
    fun `GET api weights id 는 단건을 반환한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightFinder.findById(accountId, 1L)).thenReturn(weight)

        mvc.get()
            .uri("/weights/{id}", 1)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("weights-get"))
    }

    @Test
    fun `PUT api weights id 는 수정한다`() {
        val updated = Weight(accountId, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new", 1L)
        `when`(weightRecorder.update(accountId, 1L, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new")).thenReturn(
            updated
        )

        mvc.put()
            .uri("/weights/{id}", 1)
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"value":74.0,"recordedAt":"2025-01-02","memo":"new"}""")
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("weights-update"))
    }

    @Test
    fun `DELETE api weights id 는 삭제한다`() {
        mvc.delete()
            .uri("/weights/{id}", 1)
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatus(HttpStatus.NO_CONTENT)
            .apply(document("weights-delete"))

        verify(weightRecorder).delete(accountId, 1L)
    }

    @Test
    fun `GET api weights graph 는 그래프 데이터를 반환한다`() {
        val weights = listOf(Weight(accountId, BigDecimal("75.0"), LocalDate.of(2025, 1, 1), null, 1L))
        `when`(weightFinder.graph(accountId, "WEEK")).thenReturn(weights)

        mvc.get()
            .uri("/weights/graph")
            .param("period", "WEEK")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("weights-graph"))
    }

    @Test
    fun `GET api weights stats 는 통계를 반환한다`() {
        val stat = WeightStat(BigDecimal("78.0"), BigDecimal("74.0"), BigDecimal("76.00"), BigDecimal("4.0"))
        `when`(weightFinder.stats(accountId)).thenReturn(stat)

        mvc.get()
            .uri("/weights/stats")
            .with(jwtAuth())
            .exchange()
            .assertThat()
            .hasStatusOk()
            .apply(document("weights-stats"))
    }

    @Test
    fun `인증 없이 접근하면 401을 반환한다`() {
        mvc.get()
            .uri("/weights")
            .exchange()
            .assertThat()
            .hasStatus(HttpStatus.UNAUTHORIZED)
            .apply(document("weights-unauthorized"))
    }
}
