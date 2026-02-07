package monster.monge.account.adapter.web.clerk

import jakarta.servlet.http.HttpServletRequest
import monster.monge.account.domain.AccountRegisterRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import tools.jackson.databind.ObjectMapper

@Component
class ClerkPayloadArgumentResolver(
    private val validator: SvixSignatureValidator,
    private val objectMapper: ObjectMapper
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(VerifiedClerkPayload::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest not found")

        // 1. 헤더 추출
        val svixId = request.getHeader("svix-id")
        val svixSignature = request.getHeader("svix-signature")
        val svixTimestamp = request.getHeader("svix-timestamp")
        
        // 2. 바디 읽기 (ContentCachingRequestWrapper 덕분에 가능)
        val payload = request.inputStream.bufferedReader().use { it.readText() }

        // 3. Svix 검증
        validator.verify(payload, svixId, svixSignature, svixTimestamp)

        // 4. Clerk JSON -> Request 변환 (Jackson)
        val jsonNode = objectMapper.readTree(payload)
        val userData = jsonNode.path("data")
        
        return AccountRegisterRequest(
            email = userData.path("email_addresses").get(0).path("email_address").asString(),
            providerId = userData.path("id").asString()
        )
    }
}