package austral.ingsisAR.snippetOperations.testCase.model.dto

sealed class BaseTestCaseDTO {
    abstract val name: String
    abstract val inputs: List<String>
    abstract val expectedOutputs: List<String>
    abstract val envs: List<TestCaseEnvDTO>
}
