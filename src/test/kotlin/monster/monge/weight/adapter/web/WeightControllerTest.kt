package monster.monge.weight.adapter.web

import monster.monge.account.application.required.AccountRepository
import monster.monge.account.domain.Account
import monster.monge.global.config.SecurityConfig
import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.provided.WeightRecorder
import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(WeightController::class)
@Import(SecurityConfig::class)
class WeightControllerTest(
    @MockitoBean private val weightRecorder: WeightRecorder,
    @MockitoBean private val weightFinder: WeightFinder,
    @MockitoBean private val accountRepository: AccountRepository,
    private val tester: MockMvcTester,
) {

    private val providerId = "clerk_user_123"
    private val accountId = 1L

    private fun jwtAuth() = jwt().jwt { it.subject(providerId).claim("accountId", accountId) }


    @Test
    fun `POST api weights 는 몸무게를 생성한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo", 1L)
        `when`(weightRecorder.record(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo"))
            .thenReturn(weight)

        tester.post()
            .uri("/weights")
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"value":75.5,"recordedAt":"2025-01-01","memo":"memo"}""")
            .assertThat()
            .hasStatus(201)
            .bodyJson()
            .extractingPath("$.id").isEqualTo(1)
    }

    @Test
    fun `GET api weights 는 목록을 반환한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 2), null, 2L),
            Weight(accountId, BigDecimal("76.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightFinder.findAll(accountId)).thenReturn(weights)

        tester.get()
            .uri("/weights")
            .with(jwtAuth())
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.length()").isEqualTo(2)
    }

    @Test
    fun `GET api weights id 는 단건을 반환한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightFinder.findById(accountId, 1L)).thenReturn(weight)

        tester.get()
            .uri("/weights/1")
            .with(jwtAuth())
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.id").isEqualTo(1)
    }

    @Test
    fun `PUT api weights id 는 수정한다`() {
        val updated = Weight(accountId, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new", 1L)
        `when`(weightRecorder.update(accountId, 1L, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new"))
            .thenReturn(updated)

        tester.put()
            .uri("/weights/1")
            .with(jwtAuth())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"value":74.0,"recordedAt":"2025-01-02","memo":"new"}""")
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.value").isEqualTo(74.0)
    }

    @Test
    fun `DELETE api weights id 는 삭제한다`() {

        tester.delete()
            .uri("/weights/1")
            .with(jwtAuth())
            .assertThat()
            .hasStatus(204)

        verify(weightRecorder).delete(accountId, 1L)
    }

    @Test
    fun `GET api weights graph 는 그래프 데이터를 반환한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("75.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightFinder.graph(accountId, "WEEK")).thenReturn(weights)

        tester.get()
            .uri("/weights/graph?period=WEEK")
            .with(jwtAuth())
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.length()").isEqualTo(1)
    }

    @Test
    fun `GET api weights stats 는 통계를 반환한다`() {
        val stat = WeightStat(BigDecimal("78.0"), BigDecimal("74.0"), BigDecimal("76.00"), BigDecimal("4.0"))
        `when`(weightFinder.stats(accountId)).thenReturn(stat)

        tester.get()
            .uri("/weights/stats")
            .with(jwtAuth())
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$.max").isEqualTo(78.0)
    }

    @Test
    fun `인증 없이 접근하면 401을 반환한다`() {
        tester.get()
            .uri("/weights")
            .assertThat()
            .hasStatus(401)
    }
}
