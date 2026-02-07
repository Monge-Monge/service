package monster.monge.account.adapter.web.clerk

import com.svix.kotlin.Webhook
import com.svix.kotlin.Webhook.Companion.SVIX_MSG_ID_KEY
import com.svix.kotlin.Webhook.Companion.SVIX_MSG_SIGNATURE_KEY
import com.svix.kotlin.Webhook.Companion.SVIX_MSG_TIMESTAMP_KEY
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.http.HttpHeaders

@Component
class SvixSignatureValidator(
    @Value($$"${svix.webhook.secret}") private val webhookSecret: String
) {
    private val webhook = Webhook(webhookSecret)

    fun verify(payload: String, svixId: String?, svixSignature: String?, svixTimestamp: String?) {
        requireNotNull(svixId) { "svixId cannot be null" }
        requireNotNull(svixSignature) { "svixSignature cannot be null" }
        requireNotNull(svixTimestamp) { "svixTimestamp cannot be null" }

        val headers = HttpHeaders.of(mapOf(
            SVIX_MSG_ID_KEY to listOf(svixId),
            SVIX_MSG_SIGNATURE_KEY to listOf(svixSignature),
            SVIX_MSG_TIMESTAMP_KEY to listOf(svixTimestamp))
        ) { _, _ -> true }

        webhook.verify(payload, headers)
    }
}