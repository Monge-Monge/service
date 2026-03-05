package monster.monge.weight.domain

import java.time.LocalDate

interface WeightRepository {
    fun save(weight: Weight): Weight
    fun deleteById(id: Long)
    fun findById(id: Long): Weight?
    fun findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        accountId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<Weight>
    fun findByAccountIdOrderByRecordedAtDesc(accountId: Long): List<Weight>
}
