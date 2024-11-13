import austral.ingsisAR.snippetOperations.integration.AssetService
import austral.ingsisAR.snippetOperations.integration.RunnerService
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.repository.SnippetRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseEnvRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseExpectedOutputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseInputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseRepository
import austral.ingsisAR.snippetOperations.testCase.service.TestCaseService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.assertFailsWith

class TestCaseServiceTest {
    private lateinit var testCaseService: TestCaseService
    private lateinit var testCaseRepository: TestCaseRepository
    private lateinit var testCaseInputRepository: TestCaseInputRepository
    private lateinit var testCaseExpectedOutputRepository: TestCaseExpectedOutputRepository
    private lateinit var testCaseEnvRepository: TestCaseEnvRepository
    private lateinit var snippetRepository: SnippetRepository
    private lateinit var assetService: AssetService
    private lateinit var runnerService: RunnerService

    @BeforeEach
    fun setUp() {
        testCaseRepository = mock(TestCaseRepository::class.java)
        testCaseInputRepository = mock(TestCaseInputRepository::class.java)
        testCaseExpectedOutputRepository = mock(TestCaseExpectedOutputRepository::class.java)
        testCaseEnvRepository = mock(TestCaseEnvRepository::class.java)
        snippetRepository = mock(SnippetRepository::class.java)
        assetService = mock(AssetService::class.java)
        runnerService = mock(RunnerService::class.java)
        testCaseService =
            TestCaseService(
                testCaseRepository,
                testCaseInputRepository,
                testCaseExpectedOutputRepository,
                testCaseEnvRepository,
                snippetRepository,
                assetService,
                runnerService,
            )
    }

    @Test
    fun `deleteTestCase should delete a test case`() {
        val testCaseId = "1"

        testCaseService.deleteTestCase(testCaseId)

        verify(testCaseRepository).deleteById(testCaseId)
    }

    @Test
    fun `runTestCase should throw NotFoundException if test case not found`() {
        val testCaseId = "1"
        val token = "testToken"

        `when`(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty())

        assertFailsWith<NotFoundException> {
            testCaseService.runTestCase(testCaseId, token)
        }
    }
}
