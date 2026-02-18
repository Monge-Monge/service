package monster.monge.weight.application.provided

import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat

interface WeightFinder {

    fun findAll(accountId: Long): List<Weight>
    fun findById(accountId: Long, id: Long): Weight
    fun graph(accountId: Long, period: String): List<Weight>
    fun stats(accountId: Long): WeightStat
}