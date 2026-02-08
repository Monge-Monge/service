package monster.monge.account.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.AbstractAggregateRoot

@Entity
@Table(name = "accounts")
class Account(
    val email: String,
    val providerId: String,
    @Id
    @GeneratedValue
    val id: Long? = null,
) {

    companion object {
        fun from(request: AccountRegisterRequest) =
            Account(request.email, request.providerId)
    }
}
