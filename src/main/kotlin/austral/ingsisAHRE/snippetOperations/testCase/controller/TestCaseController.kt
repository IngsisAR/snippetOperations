package austral.ingsisAHRE.snippetOperations.testCase.controller

import austral.ingsisAHRE.snippetOperations.testCase.model.dto.CreateUpdateTestCaseDTO
import austral.ingsisAHRE.snippetOperations.testCase.model.dto.GetTestCaseDTO
import austral.ingsisAHRE.snippetOperations.testCase.model.dto.GetTestRunResponseDTO
import austral.ingsisAHRE.snippetOperations.testCase.service.TestCaseService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("tests")
@Validated
class TestCaseController(
    @Autowired
    private val testCaseService: TestCaseService,
) {
    @PostMapping
    fun createOrUpdateTestCase(
        @RequestBody @Valid testCase: CreateUpdateTestCaseDTO,
    ): ResponseEntity<GetTestCaseDTO> {
        return ResponseEntity.ok(testCaseService.createOrUpdateTestCase(testCase))
    }

    @DeleteMapping("/{testCaseId}")
    fun deleteTestCase(
        @PathVariable testCaseId: String,
    ): ResponseEntity<Void> {
        testCaseService.deleteTestCase(testCaseId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{snippetId}")
    fun getSnippetTestCases(
        @PathVariable snippetId: String,
    ): ResponseEntity<List<GetTestCaseDTO>> {
        return ResponseEntity.ok(testCaseService.getSnippetTestCases(snippetId))
    }

    @PostMapping("/run/{testCaseId}")
    fun runTestCase(
        @PathVariable testCaseId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GetTestRunResponseDTO> {
        return ResponseEntity.ok(testCaseService.runTestCase(testCaseId, jwt.tokenValue))
    }
}
