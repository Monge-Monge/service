package monster.monge.weight.application.provided

import monster.monge.weight.domain.Weight
import java.math.BigDecimal
import java.time.LocalDate

interface WeightRecorder {
    fun record(accountId: Long, value: BigDecimal, recordedAt: LocalDate, memo: String?): Weight
    fun update(accountId: Long, id: Long, value: BigDecimal, recordedAt: LocalDate, memo: String?): Weight
    fun delete(accountId: Long, id: Long)

}
