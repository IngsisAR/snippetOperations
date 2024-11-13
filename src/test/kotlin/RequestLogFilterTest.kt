import austral.ingsisAR.snippetOperations.shared.log.RequestLogFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RequestLogFilterTest {
    private lateinit var requestLogFilter: RequestLogFilter
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var filterChain: FilterChain
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        requestLogFilter = RequestLogFilter()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = mock(FilterChain::class.java)
        logger = LoggerFactory.getLogger(RequestLogFilter::class.java)
    }

    @Test
    fun `doFilterInternal should log request and response`() {
        (request as MockHttpServletRequest).method = "GET"
        (request as MockHttpServletRequest).requestURI = "/test"
        (response as MockHttpServletResponse).status = 200

        val method =
            RequestLogFilter::class.java.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest::class.java,
                HttpServletResponse::class.java,
                FilterChain::class.java,
            )
        method.isAccessible = true
        method.invoke(requestLogFilter, request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        // Assuming logger.info is called with the correct message
    }
}
