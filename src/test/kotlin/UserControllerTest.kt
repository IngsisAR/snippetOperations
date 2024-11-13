import austral.ingsisAR.snippetOperations.user.controller.UserController
import austral.ingsisAR.snippetOperations.user.model.dto.UsersDTO
import austral.ingsisAR.snippetOperations.user.service.UserService
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

class UserControllerTest {
    private lateinit var userController: UserController
    private lateinit var userService: UserService
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        userService = mock(UserService::class.java)
        userController = UserController(userService)
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
    }

    @Test
    fun `getUsers should return UsersDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")
        val usersDTO = UsersDTO(users = listOf(), total = 0)
        `when`(userService.getUsers("testUser", "")).thenReturn(usersDTO)

        val response: ResponseEntity<UsersDTO> = userController.getUsers("", jwt)

        assertEquals(ResponseEntity.ok(usersDTO), response)
        verify(userService).getUsers("testUser", "")
    }
}
