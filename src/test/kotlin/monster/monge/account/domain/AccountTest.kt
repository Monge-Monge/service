package monster.monge.account.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccountTest {

    @Test
    fun `Account 정상 생성`() {
        val account = Account.from(AccountRegisterRequest("test@test.test", "testProviderId"))

        assertThat(account.email).isEqualTo("test@test.test")
        assertThat(account.providerId).isEqualTo("testProviderId")
    }
}