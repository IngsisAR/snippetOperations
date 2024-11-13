package austral.ingsisAR.snippetOperations.testCase.service

import austral.ingsisAR.snippetOperations.integration.AssetService
import austral.ingsisAR.snippetOperations.integration.RunOutputDTO
import austral.ingsisAR.snippetOperations.integration.RunnerService
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import austral.ingsisAR.snippetOperations.snippet.repository.SnippetRepository
import austral.ingsisAR.snippetOperations.testCase.model.dto.CreateUpdateTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestRunResponseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.TestCaseEnvDTO
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCase
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseEnv
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseExpectedOutput
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseInput
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseEnvRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseExpectedOutputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseInputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class TestCaseServiceTest {
    @Autowired
    private lateinit var testCaseService: TestCaseService

    @Autowired
    private lateinit var testCaseRepository: TestCaseRepository

    @Autowired
    private lateinit var testCaseInputRepository: TestCaseInputRepository

    @Autowired
    private lateinit var testCaseExpectedOutputRepository: TestCaseExpectedOutputRepository

    @Autowired
    private lateinit var testCaseEnvRepository: TestCaseEnvRepository

    @Autowired
    private lateinit var snippetRepository: SnippetRepository

    @MockBean
    private lateinit var assetService: AssetService

    @MockBean
    private lateinit var runnerService: RunnerService

    @BeforeEach
    fun setUp() {
        val snippet = Snippet("testSnippet", "Printscript")
        snippetRepository.save(snippet)
        val newTestCase = testCaseRepository.save(TestCase("testcase", snippet))
        testCaseInputRepository.save(TestCaseInput("input", newTestCase))
        testCaseExpectedOutputRepository.save(TestCaseExpectedOutput("output", newTestCase))
        testCaseEnvRepository.save(TestCaseEnv("key", "env", newTestCase))
    }

    @AfterEach
    fun tearDown() {
        testCaseRepository.deleteAll()
        snippetRepository.deleteAll()
    }

    @Test
    fun `createTest creates a test case`() {
        val snippet = snippetRepository.findAll().first()
        val testcase =
            CreateUpdateTestCaseDTO(
                null,
                snippet.id!!,
                "testcase",
                listOf("input"),
                listOf("output"),
                listOf(TestCaseEnvDTO("key", "env")),
            )

        val response = testCaseService.createOrUpdateTestCase(testcase)
        val expected =
            GetTestCaseDTO(
                response.id,
                snippet.id!!,
                "testcase",
                listOf("input"),
                listOf("output"),
                "key=env",
            )

        assertEquals(expected, response)
    }

    @Test
    fun `updateTest updates a test case`() {
        val previousTestCase = testCaseRepository.findAll().first()
        val snippet = snippetRepository.findAll().first()
        val testcase =
            CreateUpdateTestCaseDTO(
                previousTestCase.id!!,
                snippet.id!!,
                "testcase",
                listOf("input"),
                listOf("output"),
                listOf(TestCaseEnvDTO("key", "env")),
            )

        val expected =
            GetTestCaseDTO(
                previousTestCase.id!!,
                snippet.id!!,
                "testcase",
                listOf("input"),
                listOf("output"),
                "key=env",
            )

        val response = testCaseService.createOrUpdateTestCase(testcase)

        assertEquals(expected, response)
    }

    @Test
    fun `createTestCase throws NotFoundException when snippet is not found`() {
        val testcase =
            CreateUpdateTestCaseDTO(
                null,
                "nonexistentSnippetId",
                "testcase",
                listOf("input"),
                listOf("output"),
                listOf(TestCaseEnvDTO("key", "env")),
            )

        assertThrows(NotFoundException::class.java) {
            testCaseService.createOrUpdateTestCase(testcase)
        }
    }

    @Test
    fun `updateTestCase throws NotFoundException with bad test id`() {
        val testcase =
            CreateUpdateTestCaseDTO(
                "nonexistentTestCaseId",
                "nonexistentSnippetId",
                "testcase",
                listOf("input"),
                listOf("output"),
                listOf(TestCaseEnvDTO("key", "env")),
            )

        assertThrows(NotFoundException::class.java) {
            testCaseService.createOrUpdateTestCase(testcase)
        }
    }

    @Test
    fun `updateTest updates a test case with new name`() {
        val previousTestCase = testCaseRepository.findAll().first()
        val snippet = snippetRepository.findAll().first()
        val testcase =
            CreateUpdateTestCaseDTO(
                previousTestCase.id!!,
                snippet.id!!,
                "testcase1",
                listOf("input"),
                listOf("output"),
                listOf(TestCaseEnvDTO("key", "env")),
            )

        val expected =
            GetTestCaseDTO(
                previousTestCase.id!!,
                snippet.id!!,
                "testcase1",
                listOf("input"),
                listOf("output"),
                "key=env",
            )

        val response = testCaseService.createOrUpdateTestCase(testcase)

        assertEquals(expected, response)
    }

    @Test
    fun `runTestCase runs case test happy path`() {
        // Arrange
        val testCase = testCaseRepository.findAll().first()
        val expectedOutputs = testCase.expectedOutputs.map { it.output }

        // Creamos un RunOutputDTO con los outputs esperados y sin errores
        val runOutputResolve =
            RunOutputDTO(
                outputs = expectedOutputs,
                errors = listOf(),
            )

        // Creamos un ResponseEntity que simula la respuesta del servicio de snippet
        val snippetContent = ResponseEntity.ok("snippet")

        // Creamos un ResponseEntity que simula la respuesta del servicio runner
        val runnerResult = ResponseEntity.ok(runOutputResolve)

        // Configuramos los mocks para devolver estos valores
        `when`(assetService.getSnippet(testCase.snippet.id!!)).thenReturn(snippetContent)
        `when`(runnerService.runSnippet(anyString(), anyList(), anyList(), anyString())).thenReturn(runnerResult)

        // Act
        val response = testCaseService.runTestCase(testCase.id!!, "token")

        // Expected response
        val expected =
            GetTestRunResponseDTO(
                passed = true,
                message = "",
            )

        // Assert
        assertEquals(expected, response)
    }
}
