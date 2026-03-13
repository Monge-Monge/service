package monster.monge.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {
                configurationSource = UrlBasedCorsConfigurationSource().apply {
                    registerCorsConfiguration("/**", CorsConfiguration().apply {
                        allowedOrigins = listOf("https://*.monge.monster")
                        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        allowedHeaders = listOf("*")
                        allowCredentials = true
                    })
                }
            }
            csrf {
                ignoringRequestMatchers("/webhooks/clerk/**")
            }
            authorizeHttpRequests {
                authorize("/webhooks/clerk/**", permitAll)
                authorize("/docs/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
            exceptionHandling {
                authenticationEntryPoint = { _, response, _ ->
                    response.status = 401
                    response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                    response.writer.write("""{"type":"about:blank","title":"Unauthorized","status":401,"detail":"Unauthorized"}""")
                }
                accessDeniedHandler = { _, response, _ ->
                    response.status = 403
                    response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
                    response.writer.write("""{"type":"about:blank","title":"Forbidden","status":403,"detail":"Access denied"}""")
                }
            }
        }

        return http.build()
    }
}
