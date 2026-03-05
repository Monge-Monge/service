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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Weight) return false
        return accountId == other.accountId && 
               value == other.value && 
               recordedAt == other.recordedAt && 
               memo == other.memo && 
               id == other.id
    }

    override fun hashCode(): Int {
        var result = accountId.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + recordedAt.hashCode()
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
