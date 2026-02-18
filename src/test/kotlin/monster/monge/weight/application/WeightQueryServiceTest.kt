package monster.monge.weight.application

import monster.monge.weight.application.required.WeightRepository
import monster.monge.weight.domain.Weight
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class WeightQueryServiceTest {
    @Mock
    lateinit var weightRepository: WeightRepository

    @InjectMocks
    lateinit var weightQueryService: WeightQueryService

    private val accountId = 1L


    @Test
    fun `findAll은 accountId의 모든 기록을 반환한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 2), null, 2L),
            Weight(accountId, BigDecimal("76.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)).thenReturn(weights)

        val result = weightQueryService.findAll(accountId)

        assertThat(result).hasSize(2)
    }

    @Test
    fun `findById는 해당 Weight를 반환한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightRepository.findById(1L)).thenReturn(Optional.of(weight))

        val result = weightQueryService.findById(accountId, 1L)

        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    fun `findById는 존재하지 않는 id에 대해 예외를 발생시킨다`() {
        `when`(weightRepository.findById(999L)).thenReturn(Optional.empty())

        assertThatThrownBy { weightQueryService.findById(accountId, 999L) }
            .isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun `findById는 다른 사용자의 기록에 대해 예외를 발생시킨다`() {
        val weight = Weight(99L, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightRepository.findById(1L)).thenReturn(Optional.of(weight))

        assertThatThrownBy { weightQueryService.findById(accountId, 1L) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `graph는 잘못된 period에 대해 예외를 발생시킨다`() {
        assertThatThrownBy { weightQueryService.graph(accountId, "INVALID") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `graph는 WEEK period에 대해 1주간 데이터를 반환한다`() {
        val now = LocalDate.now()
        val from = now.minusWeeks(1)
        `when`(weightRepository.findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(accountId, from, now))
            .thenReturn(listOf(Weight(accountId, BigDecimal("75.0"), now, null, 1L)))

        val result = weightQueryService.graph(accountId, "WEEK")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `stats는 통계를 계산한다`() {
        val weights = listOf(
            Weight(accountId, BigDecimal("78.0"), LocalDate.of(2025, 1, 3), null, 3L),
            Weight(accountId, BigDecimal("76.0"), LocalDate.of(2025, 1, 2), null, 2L),
            Weight(accountId, BigDecimal("74.0"), LocalDate.of(2025, 1, 1), null, 1L),
        )
        `when`(weightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)).thenReturn(weights)

        val result = weightQueryService.stats(accountId)

        assertThat(result.max).isEqualByComparingTo(BigDecimal("78.0"))
        assertThat(result.min).isEqualByComparingTo(BigDecimal("74.0"))
        assertThat(result.average).isEqualByComparingTo(BigDecimal("76.00"))
        assertThat(result.change).isEqualByComparingTo(BigDecimal("4.0"))
    }

    @Test
    fun `stats는 기록이 없을 때 예외를 발생시킨다`() {
        `when`(weightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)).thenReturn(emptyList())

        assertThatThrownBy { weightQueryService.stats(accountId) }
            .isInstanceOf(NoSuchElementException::class.java)
    }
}