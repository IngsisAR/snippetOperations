package austral.ingsisAR.snippetOperations.run.service

import austral.ingsisAR.snippetOperations.integration.RunnerService
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.service.RuleService
import austral.ingsisAR.snippetOperations.run.model.dto.SnippetContent
import austral.ingsisAR.snippetOperations.shared.exception.ConflictException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RunService
    @Autowired
    constructor(
        private val ruleService: RuleService,
        private val runnerService: RunnerService,
    ) {
        private val logger: Logger = LoggerFactory.getLogger(RunService::class.java)

        fun formatSnippet(
            snippet: SnippetContent,
            userId: String,
            token: String,
        ): String {
            logger.info("Formatting Snippet with User rules")
            val rules = ruleService.getUserRulesByType(userId, RuleType.FORMATTING)

            val result =
                runnerService.formatSnippet(
                    content = snippet.content,
                    language = snippet.language,
                    formatterRules = ruleService.parseFormattingRules(rules),
                    token = token,
                )
            if (!result.statusCode.is2xxSuccessful) {
                logger.error("Error formatting Snippet content ${snippet.content}")
                throw ConflictException("Error formatting Snippet content")
            }

            return result.body!!
        }
    }
