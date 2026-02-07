package monster.monge.global.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.ContentCachingRequestWrapper

@Configuration
class WebConfig : WebMvcConfigurer {
    @Bean
    fun contentCachingFilter(): FilterRegistrationBean<Filter> =
        FilterRegistrationBean<Filter>(object : OncePerRequestFilter() {
            override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
                // 특정 경로일 때만 바디를 캐싱하는 Wrapper로 감쌈
                chain.doFilter(ContentCachingRequestWrapper(req, 0), res)
            }
        }).apply {
            addUrlPatterns("/api/webhooks/clerk/*")
        }

}