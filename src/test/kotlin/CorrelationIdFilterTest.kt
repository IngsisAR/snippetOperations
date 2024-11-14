import austral.ingsisAR.snippetOperations.shared.log.CorrelationIdFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID
import kotlin.test.assertNull

class CorrelationIdFilterTest {
    private lateinit var correlationIdFilter: CorrelationIdFilter
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var filterChain: FilterChain

    @BeforeEach
    fun setUp() {
        correlationIdFilter = CorrelationIdFilter()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = mock(FilterChain::class.java)
    }

    @Test
    fun `doFilterInternal should set correlation id from header`() {
        val correlationId = UUID.randomUUID().toString()
        (request as MockHttpServletRequest).addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId)
        val method =
            CorrelationIdFilter::class.java.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest::class.java,
                HttpServletResponse::class.java,
                FilterChain::class.java,
            )
        method.isAccessible = true
        method.invoke(correlationIdFilter, request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY))
    }

    @Test
    fun `doFilterInternal should generate correlation id if not present in header`() {
        val method =
            CorrelationIdFilter::class.java.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest::class.java,
                HttpServletResponse::class.java,
                FilterChain::class.java,
            )
        method.isAccessible = true
        method.invoke(correlationIdFilter, request, response, filterChain)

        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY)
        verify(filterChain).doFilter(request, response)
        assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY))
    }
}
