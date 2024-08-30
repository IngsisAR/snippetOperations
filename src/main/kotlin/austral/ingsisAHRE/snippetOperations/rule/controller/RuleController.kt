package austral.ingsisAHRE.snippetOperations.rule.controller

import austral.ingsisAHRE.snippetOperations.rule.model.dto.GetUserRuleDTO
import austral.ingsisAHRE.snippetOperations.rule.model.dto.UpdateUserRuleDTO
import austral.ingsisAHRE.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAHRE.snippetOperations.rule.service.RuleService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rules")
@Validated
class RuleController(
    @Autowired
    private val ruleService: RuleService,
) {
    @PostMapping("/default")
    fun createUserDefaultRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        ruleService.createUserDefaultRules(jwt.subject)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{ruleType}")
    fun getRules(
        @PathVariable("ruleType") ruleType: RuleType,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<GetUserRuleDTO>> {
        return ResponseEntity.ok(ruleService.getUserRulesByType(jwt.subject, ruleType))
    }

    @PutMapping
    suspend fun updateUserRules(
        @RequestBody @Valid userRules: List<UpdateUserRuleDTO>,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<GetUserRuleDTO>> {
        return ResponseEntity.ok(ruleService.updateUserRules(jwt.subject, userRules))
    }
}
