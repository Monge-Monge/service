package monster.monge.account.domain

data class AccountRegisterRequest(
    val email: String,
    val providerId: String
)
