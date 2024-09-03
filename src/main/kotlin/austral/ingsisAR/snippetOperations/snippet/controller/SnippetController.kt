package austral.ingsisAR.snippetOperations.snippet.controller

import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetPaginatedSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.ShareSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.UpdateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.service.SnippetService
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("snippets")
@Validated
class SnippetController(
    @Autowired
    private val snippetService: SnippetService,
) {
    @PostMapping
    fun createSnippet(
        @Valid @RequestBody snippet: CreateSnippetDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GetSnippetDTO> {
        return ResponseEntity.ok(snippetService.createSnippet(snippet, jwt.subject, jwt.tokenValue))
    }

    @GetMapping
    fun getSnippets(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("page_number") pageNumber: Int,
        @RequestParam("page_size") pageSize: Int,
    ): ResponseEntity<GetPaginatedSnippetWithStatusDTO> {
        return ResponseEntity.ok(snippetService.getSnippets(jwt.subject, jwt.tokenValue, pageNumber, pageSize))
    }

    @PutMapping("/{snippetId}")
    fun updateSnippet(
        @PathVariable("snippetId") snippetId: String,
        @Valid @RequestBody snippet: UpdateSnippetDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GetSnippetDTO> {
        return ResponseEntity.ok(snippetService.updateSnippet(snippetId, snippet, jwt.tokenValue))
    }

    @GetMapping("/{snippetId}")
    fun getSnippetById(
        @PathVariable("snippetId") snippetId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<GetSnippetDTO> {
        return ResponseEntity.ok(snippetService.getSnippetById(snippetId, jwt.subject, jwt.tokenValue))
    }

    @DeleteMapping("/{snippetId}")
    fun deleteSnippet(
        @PathVariable("snippetId") snippetId: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        snippetService.deleteSnippet(snippetId, jwt.tokenValue)
        return ResponseEntity.ok().build()
    }

    @PostMapping("share")
    fun shareSnippet(
        @Valid @RequestBody snippet: ShareSnippetDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        snippetService.shareSnippet(snippet, jwt.tokenValue)
        return ResponseEntity.ok().build()
    }
}
