package austral.ingsisAR.snippetOperations.run.controller

import austral.ingsisAR.snippetOperations.run.model.dto.SnippetContent
import austral.ingsisAR.snippetOperations.run.service.RunService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/run")
@Validated
class RunController(
    @Autowired
    private val runService: RunService,
) {
    @PostMapping("/format")
    fun formatSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody snippet: SnippetContent,
    ): ResponseEntity<String> {
        return ResponseEntity.ok(runService.formatSnippet(snippet, jwt.subject, jwt.tokenValue))
    }
}
