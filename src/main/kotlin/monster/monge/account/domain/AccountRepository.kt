package monster.monge.account.domain

import monster.monge.account.domain.Account

interface AccountRepository {
    fun save(account: Account): Account
    fun findById(id: Long): Account?
}
