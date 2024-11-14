import austral.ingsisAR.snippetOperations.run.controller.RunController
import austral.ingsisAR.snippetOperations.run.model.dto.SnippetContent
import austral.ingsisAR.snippetOperations.run.service.RunService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

class RunControllerTest {
    private lateinit var runController: RunController
    private lateinit var runService: RunService
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        runService = mock(RunService::class.java)
        runController = RunController(runService)
        mockMvc = MockMvcBuilders.standaloneSetup(runController).build()
    }

    @Test
    fun `formatSnippet should return formatted snippet`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")
        `when`(jwt.tokenValue).thenReturn("testToken")
        val snippetContent = SnippetContent(content = "println('Hello, World!')", language = "Kotlin")
        `when`(runService.formatSnippet(snippetContent, "testUser", "testToken")).thenReturn("formattedSnippet")

        val response: ResponseEntity<String> = runController.formatSnippet(jwt, snippetContent)

        assertEquals(ResponseEntity.ok("formattedSnippet"), response)
        verify(runService).formatSnippet(snippetContent, "testUser", "testToken")
    }
}
