package monster.monge.weight.infrastructure.persistence

import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class WeightPersistenceAdapter(
    private val jpaWeightRepository: JpaWeightRepository
) : WeightRepository {
    override fun save(weight: Weight): Weight = jpaWeightRepository.save(weight)
    override fun deleteById(id: Long) = jpaWeightRepository.deleteById(id)
    override fun findById(id: Long): Weight? = jpaWeightRepository.findById(id).orElse(null)
    override fun findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(
        accountId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<Weight> = jpaWeightRepository.findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(accountId, from, to)
    override fun findByAccountIdOrderByRecordedAtDesc(accountId: Long): List<Weight> = 
        jpaWeightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)
}
