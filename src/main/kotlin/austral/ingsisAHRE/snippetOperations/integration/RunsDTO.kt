package austral.ingsisAHRE.snippetOperations.integration

import austral.ingsisAHRE.snippetOperations.testCase.model.dto.TestCaseEnvDTO

data class RunOutputDTO(
    val outputs: List<String>,
    val errors: List<String>,
)

data class CreateRunDTO(
    val content: String,
    val inputs: List<String>,
    val envs: List<TestCaseEnvDTO>,
)

data class FormatSnippetRequestDTO(
    val content: String,
    val formatterRules: FormatterRulesDTO,
)

data class FormatterRulesDTO(
    var spaceBeforeColon: Int? = null,
    var spaceAfterColon: Int? = null,
    var spacesInAssignSymbol: Int? = null,
    var lineJumpBeforePrintln: Int? = null,
    var identationInsideConditionals: Int? = null,
)
