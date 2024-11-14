import austral.ingsisAR.snippetOperations.testCase.controller.TestCaseController
import austral.ingsisAR.snippetOperations.testCase.model.dto.CreateUpdateTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestRunResponseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.TestCaseEnvDTO
import austral.ingsisAR.snippetOperations.testCase.service.TestCaseService
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

class TestCaseControllerTest {
    private lateinit var testCaseController: TestCaseController
    private lateinit var testCaseService: TestCaseService
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        testCaseService = mock(TestCaseService::class.java)
        testCaseController = TestCaseController(testCaseService)
        mockMvc = MockMvcBuilders.standaloneSetup(testCaseController).build()
    }

    @Test
    fun `createOrUpdateTestCase should return created or updated test case`() {
        val testCase =
            CreateUpdateTestCaseDTO(
                id = "1",
                snippetId = "snippetId",
                name = "testName",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
                envs = listOf(TestCaseEnvDTO("env1", "value1")),
            )
        val getTestCaseDTO =
            GetTestCaseDTO(
                id = "1",
                snippetId = "snippetId",
                name = "testName",
                inputs = listOf("input1", "input2"),
                expectedOutputs = listOf("output1", "output2"),
                envs = "envs",
            )
        `when`(testCaseService.createOrUpdateTestCase(testCase)).thenReturn(getTestCaseDTO)

        val response: ResponseEntity<GetTestCaseDTO> = testCaseController.createOrUpdateTestCase(testCase)

        assertEquals(ResponseEntity.ok(getTestCaseDTO), response)
        verify(testCaseService).createOrUpdateTestCase(testCase)
    }

    @Test
    fun `deleteTestCase should return no content`() {
        val testCaseId = "testCaseId"

        val response: ResponseEntity<Void> = testCaseController.deleteTestCase(testCaseId)

        assertEquals(ResponseEntity.noContent().build(), response)
        verify(testCaseService).deleteTestCase(testCaseId)
    }

    @Test
    fun `getSnippetTestCases should return list of test cases`() {
        val snippetId = "snippetId"
        val testCases =
            listOf(
                GetTestCaseDTO(
                    id = "1",
                    snippetId = "snippetId",
                    name = "testName",
                    inputs = listOf("input1", "input2"),
                    expectedOutputs = listOf("output1", "output2"),
                    envs = "envs",
                ),
            )
        `when`(testCaseService.getSnippetTestCases(snippetId)).thenReturn(testCases)

        val response: ResponseEntity<List<GetTestCaseDTO>> = testCaseController.getSnippetTestCases(snippetId)

        assertEquals(ResponseEntity.ok(testCases), response)
        verify(testCaseService).getSnippetTestCases(snippetId)
    }

    @Test
    fun `runTestCase should return test run response`() {
        val testCaseId = "testCaseId"
        val jwt = mock(Jwt::class.java)
        `when`(jwt.tokenValue).thenReturn("testToken")
        val testRunResponse =
            GetTestRunResponseDTO(
                passed = true,
                message = "Test passed successfully",
            )
        `when`(testCaseService.runTestCase(testCaseId, "testToken")).thenReturn(testRunResponse)

        val response: ResponseEntity<GetTestRunResponseDTO> = testCaseController.runTestCase(testCaseId, jwt)

        assertEquals(ResponseEntity.ok(testRunResponse), response)
        verify(testCaseService).runTestCase(testCaseId, "testToken")
    }
}
