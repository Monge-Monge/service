package monster.monge.weight.application

import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.required.WeightRepository
import monster.monge.weight.domain.Weight
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class WeightModifyServiceTest {

    @Mock
    lateinit var weightRepository: WeightRepository

    @Mock
    lateinit var weightFinder: WeightFinder

    @InjectMocks
    lateinit var weightModifyService: WeightModifyService

    private val accountId = 1L

    @Test
    fun `record는 Weight를 생성하고 저장한다`() {
        val saved = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo", 1L)
        `when`(weightRepository.save(any(Weight::class.java))).thenReturn(saved)

        val result = weightModifyService.record(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo")

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.value).isEqualByComparingTo(BigDecimal("75.5"))
        verify(weightRepository).save(any(Weight::class.java))
    }

    @Test
    fun `update는 Weight를 수정하고 저장한다`() {
        val existing = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "old", 1L)
        val updated = Weight(accountId, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new", 1L)
        `when`(weightFinder.findById(accountId, 1L)).thenReturn(existing)
        `when`(weightRepository.save(any(Weight::class.java))).thenReturn(updated)

        val result = weightModifyService.update(accountId, 1L, BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new")

        assertThat(result.value).isEqualByComparingTo(BigDecimal("74.0"))
        assertThat(result.memo).isEqualTo("new")
    }

    @Test
    fun `delete는 Weight를 삭제한다`() {
        val weight = Weight(accountId, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), null, 1L)
        `when`(weightFinder.findById(accountId, 1L)).thenReturn(weight)

        weightModifyService.delete(accountId, 1L)

        verify(weightRepository).delete(weight)
    }
}
