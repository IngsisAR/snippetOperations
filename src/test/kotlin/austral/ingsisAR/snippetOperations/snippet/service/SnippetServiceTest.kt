package austral.ingsisAR.snippetOperations.snippet.service

import austral.ingsisAR.snippetOperations.integration.AssetService
import austral.ingsisAR.snippetOperations.integration.GetPaginatedSnippetPermissionsDTO
import austral.ingsisAR.snippetOperations.integration.GetSnippetPermissionsDTO
import austral.ingsisAR.snippetOperations.integration.SnippetPermissionService
import austral.ingsisAR.snippetOperations.redis.producer.LintRequestProducer
import austral.ingsisAR.snippetOperations.shared.exception.ConflictException
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetPermissionDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetPaginatedSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.ShareSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.UpdateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import austral.ingsisAR.snippetOperations.snippet.model.entity.UserSnippet
import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAR.snippetOperations.snippet.repository.SnippetRepository
import austral.ingsisAR.snippetOperations.snippet.repository.UserSnippetRepository
import austral.ingsisAR.snippetOperations.user.service.UserService
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class SnippetServiceTest {
    @Autowired
    private lateinit var snippetService: SnippetService

    @Autowired
    private lateinit var snippetRepository: SnippetRepository

    @Autowired
    private lateinit var userSnippetRepository: UserSnippetRepository

    @MockBean
    private lateinit var assetService: AssetService

    @MockBean
    private lateinit var permissionService: SnippetPermissionService

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var lintRequestProducer: LintRequestProducer

    @BeforeEach
    fun setUp() {
        val snippet = Snippet("testSnippet", "Printscript")
        snippetRepository.save(snippet)
        val userSnippet = UserSnippet("testUser", SnippetStatus.COMPLIANT, snippet)
        userSnippetRepository.save(userSnippet)
    }

    @AfterEach
    fun tearDown() {
        snippetRepository.deleteAll()
        userSnippetRepository.deleteAll()
    }

    @Test
    fun `deleteSnippet should delete snippet and its content and permissions`() {
        val snippet = snippetRepository.findAll().first()
        `when`(assetService.deleteSnippet(anyString())).thenReturn(ResponseEntity.ok().build())
        `when`(permissionService.deleteSnippetPermissions(anyString(), anyString())).thenReturn(ResponseEntity.ok().build())

        snippetService.deleteSnippet(snippet.id!!, "")
        assertEquals(0, snippetRepository.findAll().size)
    }

    @Test
    fun `createSnippet should create a snippet and its content and permissions`() {
        val createSnippetDTO =
            CreateSnippetDTO(
                name = "testSnippet",
                language = "Kotlin",
                content = "fun main() { println(\"Hello, World!\") }",
            )
        val userId = "testUser"
        val token = "testToken"
        val snippetPermissionDTO =
            CreateSnippetPermissionDTO(
                snippetId = "testSnippet",
                userId = userId,
                permissionType = "OWNER",
            )

        `when`(assetService.saveSnippet(anyString(), anyString())).thenReturn(ResponseEntity.ok().build())
        `when`(permissionService.createSnippetPermission(snippetPermissionDTO, token)).thenReturn(ResponseEntity.ok().build())
        `when`(userService.getUserById(userId)).thenReturn("testUser")
        val createdSnippet = snippetService.createSnippet(createSnippetDTO, userId, token)

        verify(assetService).saveSnippet(createdSnippet.id, createSnippetDTO.content)
        verify(permissionService).createSnippetPermission(
            CreateSnippetPermissionDTO(
                snippetId = createdSnippet.id,
                userId = userId,
                permissionType = "OWNER",
            ),
            token,
        )

        assertEquals(createSnippetDTO.name, createdSnippet.name)
        assertEquals(createSnippetDTO.language, createdSnippet.language)
        assertEquals(createSnippetDTO.content, createdSnippet.content)
        assertEquals(userId, createdSnippet.author)

        assertEquals(2, snippetRepository.findAll().size)
    }

    @Test
    fun `getSnippetById should get a snippet and its content`() {
        val snippet = snippetRepository.findAll().first()
        val targetSnippet =
            GetSnippetDTO(
                id = snippet.id!!,
                name = snippet.name,
                content = "fun main() { println(\"Hello, World!\") }",
                language = snippet.language,
                author = "testUser",
            )

        val token = "testToken"

        `when`(assetService.getSnippet(anyString())).thenReturn(ResponseEntity.ok(targetSnippet.content))
        `when`(permissionService.getAuthorBySnippetId(anyString(), anyString())).thenReturn(ResponseEntity.ok(targetSnippet.author))
        `when`(userService.getUserById(anyString())).thenReturn(targetSnippet.author)

        val response = snippetService.getSnippetById(snippet.id!!, snippet.name, token)
        assertEquals(targetSnippet, response)
    }

    @Test
    fun `shareSnippet should create UserSnippet`() {
        val snippet = snippetRepository.findAll().first()
        val shareSnippetDTO =
            ShareSnippetDTO(
                snippetId = snippet.id!!,
                userId = "testUser2",
            )
        val createSnippetSnippetDTO =
            CreateSnippetPermissionDTO(
                snippetId = snippet.id!!,
                userId = shareSnippetDTO.userId,
                permissionType = "SHARED",
            )

        val token = "testToken"

        `when`(permissionService.createSnippetPermission(createSnippetSnippetDTO, token)).thenReturn(ResponseEntity.ok().build())

        snippetService.shareSnippet(shareSnippetDTO, "testUser", token)
        assertEquals(2, userSnippetRepository.findAll().size)
    }

    @Test
    fun `getSnippets should get user snippets`() {
        val snippet = snippetRepository.findAll().first()
        val expected =
            GetPaginatedSnippetWithStatusDTO(
                snippets =
                    listOf(
                        GetSnippetWithStatusDTO(
                            id = snippet.id!!,
                            name = snippet.name,
                            content = "fun main() { println(\"Hello, World!\") }",
                            language = snippet.language,
                            author = "testUser",
                            status = SnippetStatus.COMPLIANT,
                        ),
                    ),
                total = 1,
            )

        val snippetPermissions =
            GetPaginatedSnippetPermissionsDTO(
                permissions =
                    listOf(
                        GetSnippetPermissionsDTO(
                            snippetId = snippet.id!!,
                            authorId = "testUser",
                            id = "1",
                        ),
                    ),
                total = 1,
            )

        val token = "testToken"
        `when`(assetService.getSnippet(anyString())).thenReturn(ResponseEntity.ok(expected.snippets.first()!!.content))
        `when`(permissionService.getAllSnippetPermissions("testUser", token, 1, 1)).thenReturn(ResponseEntity.ok(snippetPermissions))
        `when`(userService.getUserById(anyString())).thenReturn(expected.snippets.first()!!.author)

        val response = snippetService.getSnippets("testUser", token, 1, 1, null)
        assertEquals(expected, response)
    }

    @Test
    fun `getSnippets should return empty when incorrect snippet name was passed`() {
        val snippet = snippetRepository.findAll().first()
        val expected =
            GetPaginatedSnippetWithStatusDTO(
                snippets = listOf(),
                total = 1,
            )

        val snippetPermissions =
            GetPaginatedSnippetPermissionsDTO(
                permissions =
                    listOf(
                        GetSnippetPermissionsDTO(
                            snippetId = snippet.id!!,
                            authorId = "testUser",
                            id = "1",
                        ),
                    ),
                total = 1,
            )

        val token = "testToken"
        `when`(permissionService.getAllSnippetPermissions("testUser", token, 1, 1)).thenReturn(ResponseEntity.ok(snippetPermissions))

        val response = snippetService.getSnippets("testUser", token, 1, 1, "incorrectSnippetName")
        assertEquals(expected, response)
    }

    @Test
    fun `getSnippets should throw ConflictException when permissions retrieval fails`() {
        val userId = "testUser"
        val token = "testToken"
        val pageNumber = 1
        val pageSize = 1

        `when`(permissionService.getAllSnippetPermissions(userId, token, pageNumber, pageSize))
            .thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.getSnippets(userId, token, pageNumber, pageSize, null)
            }

        assertEquals("Error getting snippets permissions", exception.message)
    }

    @Test
    fun `getSnippets should throw NotFoundException when snippet is not found`() {
        snippetRepository.deleteAll()
        val userId = "testUser"
        val token = "testToken"
        val pageNumber = 1
        val pageSize = 1

        val snippetPermissions =
            GetPaginatedSnippetPermissionsDTO(
                permissions =
                    listOf(
                        GetSnippetPermissionsDTO(
                            snippetId = "nonExistentSnippetId",
                            authorId = userId,
                            id = "1",
                        ),
                    ),
                total = 1,
            )

        `when`(permissionService.getAllSnippetPermissions(userId, token, pageNumber, pageSize))
            .thenReturn(ResponseEntity.ok(snippetPermissions))

        val exception =
            assertThrows<NotFoundException> {
                snippetService.getSnippets(userId, token, pageNumber, pageSize, null)
            }

        assertEquals("Snippet not found", exception.message)
    }

    @Test
    fun `getSnippets should throw ConflictException when snippet content retrieval fails`() {
        val userId = "testUser"
        val token = "testToken"
        val pageNumber = 1
        val pageSize = 1

        val snippet = snippetRepository.findAll().first()
        val snippetPermissions =
            GetPaginatedSnippetPermissionsDTO(
                permissions =
                    listOf(
                        GetSnippetPermissionsDTO(
                            snippetId = snippet.id!!,
                            authorId = userId,
                            id = "1",
                        ),
                    ),
                total = 1,
            )

        `when`(permissionService.getAllSnippetPermissions(userId, token, pageNumber, pageSize))
            .thenReturn(ResponseEntity.ok(snippetPermissions))
        `when`(assetService.getSnippet(snippet.id!!)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.getSnippets(userId, token, pageNumber, pageSize, null)
            }

        assertEquals("Error getting Snippet content", exception.message)
    }

    @Test
    fun `getSnippets should throw NotFoundException when snippet status is not found`() {
        userSnippetRepository.deleteAll()
        val userId = "testUser"
        val token = "testToken"
        val pageNumber = 1
        val pageSize = 1

        val snippet = snippetRepository.findAll().first()
        val snippetPermissions =
            GetPaginatedSnippetPermissionsDTO(
                permissions =
                    listOf(
                        GetSnippetPermissionsDTO(
                            snippetId = snippet.id!!,
                            authorId = userId,
                            id = "1",
                        ),
                    ),
                total = 1,
            )

        `when`(permissionService.getAllSnippetPermissions(userId, token, pageNumber, pageSize))
            .thenReturn(ResponseEntity.ok(snippetPermissions))
        `when`(assetService.getSnippet(snippet.id!!)).thenReturn(ResponseEntity.ok("fun main() { println(\"Hello, World!\") }"))

        val exception =
            assertThrows<NotFoundException> {
                snippetService.getSnippets(userId, token, pageNumber, pageSize, null)
            }

        assertEquals("Snippet status not found", exception.message)
    }

    @Test
    fun `deleteSnippet should throw ConflictException when permission deletion fails`() {
        val snippet = snippetRepository.findAll().first()
        val token = "testToken"

        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.ok().build())
        `when`(permissionService.deleteSnippetPermissions(snippet.id!!, token)).thenReturn(
            ResponseEntity.status(HttpStatus.CONFLICT).build(),
        )

        val exception =
            assertThrows<ConflictException> {
                snippetService.deleteSnippet(snippet.id!!, token)
            }

        assertEquals("Error deleting Snippet permissions", exception.message)
    }

    @Test
    fun `updateSnippet should update snippet content`() {
        val snippet = snippetRepository.findAll().first()
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "fun main() { println(\"Bye, World!\") }",
            )
        val token = "testToken"
        val expectedAuthor = "testUser"

        `when`(assetService.updateSnippet(snippet.id!!, updateSnippetDTO.content)).thenReturn(ResponseEntity.ok().build())
        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.ok().build())
        `when`(permissionService.getAuthorBySnippetId(snippet.id!!, token)).thenReturn(ResponseEntity.ok(expectedAuthor))
        `when`(userService.getUserById(expectedAuthor)).thenReturn(expectedAuthor)

        val newSnippet = snippetService.updateSnippet(snippet.id!!, updateSnippetDTO, token)
        assertEquals(updateSnippetDTO.content, newSnippet.content)
    }

    @Test
    fun `updateSnippet should throw ConflictException when snippet content deletion fails`() {
        val snippet = snippetRepository.findAll().first()
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "fun main() { println(\"Bye, World!\") }",
            )
        val token = "testToken"

        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.updateSnippet(snippet.id!!, updateSnippetDTO, token)
            }

        assertEquals("Error deleting Snippet content on asset service", exception.message)
    }

    @Test
    fun `updateSnippet should throw NotFoundException when snippet is not found`() {
        val snippetId = "nonExistentSnippetId"
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "fun main() { println(\"Bye, World!\") }",
            )
        val token = "testToken"
        val exception =
            assertThrows<NotFoundException> {
                snippetService.updateSnippet(snippetId, updateSnippetDTO, token)
            }

        assertEquals("Snippet not found", exception.message)
    }

    @Test
    fun `updateSnippet should throw ConflictException when asset update fails`() {
        val snippet = snippetRepository.findAll().first()
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "fun main() { println(\"Bye, World!\") }",
            )
        val token = "testToken"

        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.ok().build())
        `when`(assetService.updateSnippet(snippet.id!!, updateSnippetDTO.content)).thenReturn(
            ResponseEntity.status(HttpStatus.CONFLICT).build(),
        )

        val exception =
            assertThrows<ConflictException> {
                snippetService.updateSnippet(snippet.id!!, updateSnippetDTO, token)
            }

        assertEquals("Error updating Snippet content on asset service", exception.message)
    }

    @Test
    fun `updateSnippet should throw ConflictException when getting author fails`() {
        val snippet = snippetRepository.findAll().first()
        val updateSnippetDTO =
            UpdateSnippetDTO(
                content = "fun main() { println(\"Bye, World!\") }",
            )
        val token = "testToken"

        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.ok().build())
        `when`(assetService.updateSnippet(snippet.id!!, updateSnippetDTO.content)).thenReturn(ResponseEntity.ok().build())
        `when`(permissionService.getAuthorBySnippetId(snippet.id!!, token)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.updateSnippet(snippet.id!!, updateSnippetDTO, token)
            }

        assertEquals("Error getting Snippet author", exception.message)
    }

    @Test
    fun `createSnippet should throw ConflictException when asset service fails to save snippet content`() {
        val createSnippetDTO =
            CreateSnippetDTO(
                name = "testSnippet",
                language = "Kotlin",
                content = "fun main() { println(\"Hello, World!\") }",
            )
        val userId = "testUser"
        val token = "testToken"

        `when`(assetService.saveSnippet(anyString(), anyString())).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.createSnippet(createSnippetDTO, userId, token)
            }

        assertEquals("Error saving Snippet content on asset service", exception.message)
    }

    @Test
    fun `updateUserSnippetsStatus should update user snippets status`() {
        val userId = "testUser"
        val status = SnippetStatus.COMPLIANT

        val updatedSnippets = snippetService.updateUserSnippetsStatus(userId, status)

        assertEquals(1, updatedSnippets.size)
        updatedSnippets.forEach {
            assertEquals(status, it.status)
        }
    }

    @Test
    fun `updateUserSnippetStatusBySnippetId should update user snippet status`() {
        val snippet = snippetRepository.findAll().first()
        val userId = "testUser"
        val snippetId = snippet.id!!
        val status = SnippetStatus.COMPLIANT

        snippetService.updateUserSnippetStatusBySnippetId(userId, snippetId, status)

        val updatedSnippet = userSnippetRepository.findFirstBySnippetIdAndUserId(snippetId, userId)
        assertNotNull(updatedSnippet, "Updated snippet should not be null")
        assertEquals(status, updatedSnippet!!.status)
    }

    @Test
    fun `shareSnippet should throw ConflictException when creating snippet permissions fails`() {
        val snippet = snippetRepository.findAll().first()
        val shareSnippetDTO =
            ShareSnippetDTO(
                snippetId = snippet.id!!,
                userId = "testUser2",
            )
        val createSnippetSnippetDTO =
            CreateSnippetPermissionDTO(
                snippetId = snippet.id!!,
                userId = shareSnippetDTO.userId,
                permissionType = "SHARED",
            )
        val token = "testToken"

        `when`(
            permissionService.createSnippetPermission(
                createSnippetSnippetDTO,
                token,
            ),
        ).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.shareSnippet(shareSnippetDTO, "testUser", token)
            }

        assertEquals("Error creating snippet permissions", exception.message)
    }

    @Test
    fun `shareSnippet should throw NotFoundException when snippet is not found`() {
        val shareSnippetDTO =
            ShareSnippetDTO(
                snippetId = "nonExistentSnippetId",
                userId = "testUser2",
            )
        val token = "testToken"

        val exception =
            assertThrows<NotFoundException> {
                snippetService.shareSnippet(shareSnippetDTO, "testUser", token)
            }

        assertEquals("Snippet not found", exception.message)
    }

    @Test
    fun `deleteSnippet should throw ConflictException when deleting snippet content fails`() {
        val snippet = snippetRepository.findAll().first()
        val token = "testToken"

        `when`(assetService.deleteSnippet(snippet.id!!)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.deleteSnippet(snippet.id!!, token)
            }

        assertEquals("Error deleting Snippet content", exception.message)
    }

    @Test
    fun `deleteSnippet should throw NotFoundException when snippet is not found`() {
        val snippetId = "nonExistentSnippetId"
        val token = "testToken"

        val exception =
            assertThrows<NotFoundException> {
                snippetService.deleteSnippet(snippetId, token)
            }

        assertEquals("Snippet not found", exception.message)
    }

    @Test
    fun `getSnippetById should throw ConflictException when getting snippet author fails`() {
        val snippet = snippetRepository.findAll().first()
        val snippetId = snippet.id!!
        val userId = "testUser"
        val token = "testToken"

        `when`(assetService.getSnippet(snippetId)).thenReturn(ResponseEntity.ok("fun main() { println(\"Hello, World!\") }"))
        `when`(permissionService.getAuthorBySnippetId(snippetId, token)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.getSnippetById(snippetId, userId, token)
            }

        assertEquals("Error getting Snippet author", exception.message)
    }

    @Test
    fun `getSnippetById should throw ConflictException when getting snippet content fails`() {
        val snippet = snippetRepository.findAll().first()
        val snippetId = snippet.id!!
        val userId = "testUser"
        val token = "testToken"

        `when`(assetService.getSnippet(snippetId)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())

        val exception =
            assertThrows<ConflictException> {
                snippetService.getSnippetById(snippetId, userId, token)
            }

        assertEquals("Error getting Snippet content", exception.message)
    }

    @Test
    fun `getSnippetById should throw NotFoundException when snippet is not found`() {
        val snippetId = "nonExistentSnippetId"
        val userId = "testUser"
        val token = "testToken"

        val exception =
            assertThrows<NotFoundException> {
                snippetService.getSnippetById(snippetId, userId, token)
            }

        assertEquals("Snippet not found", exception.message)
    }

    @Test
    fun `updateUserSnippetStatusBySnippetId should throw NotFoundException when user snippet is not found`() {
        val userId = "nonExistentUser"
        val snippetId = "nonExistentSnippetId"
        val status = SnippetStatus.PENDING

        val exception =
            assertThrows<NotFoundException> {
                snippetService.updateUserSnippetStatusBySnippetId(userId, snippetId, status)
            }

        assertEquals("User Snippet not found", exception.message)
    }
}
