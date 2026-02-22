package monster.monge.account.adapter.web.clerk

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

private val log = KotlinLogging.logger {}
@Component
class ClerkClient(
    @Value($$"${clerk.base-url}") private val clerkBaseUrl: String,
    @Value($$"${clerk.api-key}")private val clerkApiKey: String,
) {
    private val restClient = RestClient.create(clerkBaseUrl)

    fun mergeAndUpdate(providerId: String, metadata: Map<String, Any>) {
        log.info { "Clerk API 호출 시작: PATCH /v1/users/${providerId}/metadata, metadata=${metadata}" }
        try {
            val response = restClient.patch()
                .uri("/v1/users/$providerId/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $clerkApiKey")
                .body(mapOf("public_metadata" to metadata))
                .retrieve()
                .body<String>()
            log.info { "Clerk API 호출 성공: PATCH /v1/users/${providerId}/metadata, Response: $response" }
        } catch (e: Exception) {
            log.error(e) {"Clerk API 호출 실패: PATCH /v1/users/${providerId}/metadata" }
            throw e
        }
    }

}
