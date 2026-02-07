package monster.monge.account.application.provided

import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegisterRequest

interface AccountRegister {
    fun register(request: AccountRegisterRequest): Account
}