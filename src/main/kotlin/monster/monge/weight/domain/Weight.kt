package monster.monge.weight.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "weights")
class Weight(
    val accountId: Long,
    val value: BigDecimal,
    val recordedAt: LocalDate,
    val memo: String? = null,
    @Id @GeneratedValue
    val id: Long? = null,
) {
    fun update(value: BigDecimal, recordedAt: LocalDate, memo: String?): Weight =
        Weight(accountId, value, recordedAt, memo, id)
}
