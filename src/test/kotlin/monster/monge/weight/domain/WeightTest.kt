package monster.monge.weight.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class WeightTest {

    @Test
    fun `Weight 정상 생성`() {
        val weight = Weight(1L, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "memo")

        assertThat(weight.accountId).isEqualTo(1L)
        assertThat(weight.value).isEqualByComparingTo(BigDecimal("75.5"))
        assertThat(weight.recordedAt).isEqualTo(LocalDate.of(2025, 1, 1))
        assertThat(weight.memo).isEqualTo("memo")
        assertThat(weight.id).isNull()
    }

    @Test
    fun `Weight 생성 시 memo는 선택이다`() {
        val weight = Weight(1L, BigDecimal("75.5"), LocalDate.of(2025, 1, 1))

        assertThat(weight.memo).isNull()
    }

    @Test
    fun `Weight update는 새 Weight를 반환한다`() {
        val weight = Weight(1L, BigDecimal("75.5"), LocalDate.of(2025, 1, 1), "old memo", 10L)
        val updated = weight.update(BigDecimal("74.0"), LocalDate.of(2025, 1, 2), "new memo")

        assertThat(updated.id).isEqualTo(10L)
        assertThat(updated.accountId).isEqualTo(1L)
        assertThat(updated.value).isEqualByComparingTo(BigDecimal("74.0"))
        assertThat(updated.recordedAt).isEqualTo(LocalDate.of(2025, 1, 2))
        assertThat(updated.memo).isEqualTo("new memo")
    }
}
