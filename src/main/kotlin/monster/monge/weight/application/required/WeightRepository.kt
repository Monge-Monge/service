package monster.monge.weight.application.required

import monster.monge.weight.domain.Weight
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface WeightRepository : JpaRepository<Weight, Long> {

    fun findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        accountId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<Weight>

    fun findByAccountIdOrderByRecordedAtDesc(accountId: Long): List<Weight>
}
