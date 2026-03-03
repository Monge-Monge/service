package monster.monge.account.application

import monster.monge.account.domain.AccountRepository
import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegisterRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AccountModifyServiceTest {

    @Mock
    lateinit var accountRepository: AccountRepository

    @InjectMocks
    lateinit var accountModifyService: AccountModifyService

    @Test
    fun `register는 Account를 생성하고 저장한다`() {
        val email = "test@test.test"
        val providerId = "testProviderId"
        val request = AccountRegisterRequest(email, providerId)
        val expectedAccount = Account(email, providerId)
        val savedAccount = Account(email, providerId, 1L)
        
        `when`(accountRepository.save(expectedAccount)).thenReturn(savedAccount)

        val result = accountModifyService.register(request)

        assertThat(result.email).isEqualTo(email)
        assertThat(result.providerId).isEqualTo(providerId)
        assertThat(result.id).isEqualTo(1L)
        verify(accountRepository).save(expectedAccount)
    }
}
