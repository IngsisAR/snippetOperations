import austral.ingsisAR.snippetOperations.snippet.controller.SnippetController
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetPaginatedSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.ShareSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.UpdateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAR.snippetOperations.snippet.service.SnippetService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.test.assertEquals

class SnippetControllerTest {
    private lateinit var snippetService: SnippetService
    private lateinit var snippetController: SnippetController

    @BeforeEach
    fun setUp() {
        snippetService = mock(SnippetService::class.java)
        snippetController = SnippetController(snippetService)
    }

    @Test
    fun `createSnippet should return GetSnippetDTO`() {
        val createSnippetDTO =
            CreateSnippetDTO(
                name = "Test Snippet",
                content = "print('Hello, World!')",
                language = "Python",
            )
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testSubject")
        `when`(jwt.tokenValue).thenReturn("testToken")

        val getSnippetDTO =
            GetSnippetDTO(
                id = "1",
                name = "Test Snippet",
                content = "print('Hello, World!')",
                language = "Python",
                author = "testSubject",
            )
        `when`(snippetService.createSnippet(createSnippetDTO, "testSubject", "testToken")).thenReturn(getSnippetDTO)

        val response = snippetController.createSnippet(createSnippetDTO, jwt)
        assertEquals(ResponseEntity.ok(getSnippetDTO), response)
    }

    @Test
    fun `getSnippets should return GetPaginatedSnippetWithStatusDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testSubject")
        `when`(jwt.tokenValue).thenReturn("testToken")

        val paginatedSnippets =
            GetPaginatedSnippetWithStatusDTO(
                snippets =
                    listOf(
                        GetSnippetWithStatusDTO(
                            id = "1",
                            name = "Test Snippet",
                            content = "print('Hello, World!')",
                            language = "PrintScript",
                            author = "testSubject",
                            status = SnippetStatus.COMPLIANT,
                        ),
                    ),
                total = 1,
            )
        `when`(snippetService.getSnippets("testSubject", "testToken", 1, 10, "SnippetTest")).thenReturn(paginatedSnippets)

        val response = snippetController.getSnippets(jwt, 1, 10, "SnippetTest")
        assertEquals(ResponseEntity.ok(paginatedSnippets), response)
    }

    @Test
    fun `updateSnippet should return GetSnippetDTO`() {
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "print('Hello, Updated World!')",
            )
        val jwt = mock(Jwt::class.java)
        `when`(jwt.tokenValue).thenReturn("testToken")

        val getSnippetDTO =
            GetSnippetDTO(
                id = "1",
                name = "Updated Snippet",
                content = "print('Hello, Updated World!')",
                language = "Python",
                author = "testSubject",
            )
        `when`(snippetService.updateSnippet("testSnippetId", updateSnippetDTO, "testToken")).thenReturn(getSnippetDTO)

        val response = snippetController.updateSnippet("testSnippetId", updateSnippetDTO, jwt)
        assertEquals(ResponseEntity.ok(getSnippetDTO), response)
    }

    @Test
    fun `getSnippetById should return GetSnippetDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testSubject")
        `when`(jwt.tokenValue).thenReturn("testToken")

        val getSnippetDTO =
            GetSnippetDTO(
                id = "1",
                name = "Test Snippet",
                content = "print('Hello, World!')",
                language = "Python",
                author = "testSubject",
            )
        `when`(snippetService.getSnippetById("1", "testSubject", "testToken")).thenReturn(getSnippetDTO)

        val response = snippetController.getSnippetById("1", jwt)
        assertEquals(ResponseEntity.ok(getSnippetDTO), response)
    }

    @Test
    fun `deleteSnippet should return ResponseEntity Void`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.tokenValue).thenReturn("testToken")

        doNothing().`when`(snippetService).deleteSnippet("1", "testToken")

        val response = snippetController.deleteSnippet("1", jwt)
        assertEquals(ResponseEntity.ok().build(), response)
    }

    @Test
    fun `shareSnippet should return ResponseEntity Void`() {
        val shareSnippetDTO =
            ShareSnippetDTO(
                snippetId = "1",
                userId = "testUser",
            )
        val jwt = mock(Jwt::class.java)
        `when`(jwt.tokenValue).thenReturn("testToken")
        `when`(jwt.subject).thenReturn("testUser")
        doNothing().`when`(snippetService).shareSnippet(shareSnippetDTO, "testUser", "testToken")

        val response = snippetController.shareSnippet(shareSnippetDTO, jwt)
        assertEquals(ResponseEntity.ok().build(), response)
    }
}
