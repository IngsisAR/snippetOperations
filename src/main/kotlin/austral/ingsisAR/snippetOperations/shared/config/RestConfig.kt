package austral.ingsisAR.snippetOperations.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate().apply {
            messageConverters.add(MappingJackson2HttpMessageConverter())
        }
    }
}
