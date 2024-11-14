import austral.ingsisAR.snippetOperations.integration.Auth0ManagementService
import austral.ingsisAR.snippetOperations.user.model.dto.UserDTO
import austral.ingsisAR.snippetOperations.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class UserServiceTest {
    @Mock
    private lateinit var auth0ManagementService: Auth0ManagementService

    @InjectMocks
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(auth0ManagementService)
    }

    @Test
    fun `getUsers should filter out the user with the given userId`() {
        val userId = "user1"
        val name = "testName"
        val users =
            listOf(
                UserDTO("user1", "nickname1"),
                UserDTO("user2", "nickname2"),
            )
        `when`(auth0ManagementService.getUsers(name)).thenReturn(ResponseEntity.ok(users))

        val result = userService.getUsers(userId, name)

        assertEquals("user2", result.users[0].user_id)
    }

    @Test
    fun `getUserById should return the correct nickname`() {
        val userId = "user1"
        val user = UserDTO("user1", "nickname1")
        `when`(auth0ManagementService.getUserById(userId)).thenReturn(ResponseEntity.ok(user))

        val result = userService.getUserById(userId)

        assertEquals("nickname1", result)
    }

    @Test
    fun `getUsers should return empty UsersDTO when response has no body`() {
        val userId = "user1"
        val name = "testName"
        `when`(auth0ManagementService.getUsers(name)).thenReturn(ResponseEntity.noContent().build())

        val result = userService.getUsers(userId, name)

        assertEquals(0, result.users.size)
    }
}
