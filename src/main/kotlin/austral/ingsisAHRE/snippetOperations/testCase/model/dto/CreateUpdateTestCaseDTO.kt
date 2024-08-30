package austral.ingsisAHRE.snippetOperations.testCase.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateUpdateTestCaseDTO(
    val id: String?,
    @field:NotBlank(message = "Snippet id is required")
    val snippetId: String,
    @field:NotBlank(message = "Name is required")
    override val name: String,
    override val inputs: List<String>,
    override val expectedOutputs: List<String>,
    override val envs: List<TestCaseEnvDTO>,
) : BaseTestCaseDTO()
