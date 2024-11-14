import austral.ingsisAR.snippetOperations.shared.config.RestConfig
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@SpringBootTest(classes = [RestConfig::class])
class RestConfigTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    fun `restTemplate should have MappingJackson2HttpMessageConverter`() {
        val hasJacksonConverter = restTemplate.messageConverters.any { it is MappingJackson2HttpMessageConverter }
        assertTrue(hasJacksonConverter)
    }
}
