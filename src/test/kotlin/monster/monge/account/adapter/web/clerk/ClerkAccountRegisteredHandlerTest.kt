package monster.monge.account.adapter.web.clerk

import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegistered
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ClerkAccountRegisteredHandlerTest {

    @Mock
    lateinit var clerkClient: ClerkClient

    @InjectMocks
    lateinit var handler: ClerkAccountRegisteredHandler

    @Test
    fun `AccountRegistered 이벤트를 처리하면 ClerkClient로 메타데이터를 업데이트한다`() {
        val account = Account("test@test.test", "testProviderId", 1L)
        val event = AccountRegistered(account)

        handler.handle(event)

        verify(clerkClient).mergeAndUpdate("testProviderId", mapOf("sub_id" to "1"))
    }
}
