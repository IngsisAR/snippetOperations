package austral.ingsisAHRE.snippetOperations.testCase.model.dto

data class GetTestCaseDTO(
    val id: String,
    val snippetId: String,
    val name: String,
    val inputs: List<String>,
    val expectedOutputs: List<String>,
    val envs: List<TestCaseEnvDTO>,
)
