package austral.ingsisAR.snippetOperations.rule.service

import austral.ingsisAR.snippetOperations.integration.FormatterRulesDTO
import austral.ingsisAR.snippetOperations.redis.event.LintRequestEvent
import austral.ingsisAR.snippetOperations.redis.event.LinterRulesDTO
import austral.ingsisAR.snippetOperations.redis.producer.LintRequestProducer
import austral.ingsisAR.snippetOperations.rule.model.dto.GetUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.dto.UpdateUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.entity.UserRule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.repository.RuleRepository
import austral.ingsisAR.snippetOperations.rule.repository.UserRuleRepository
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.model.entity.UserSnippet
import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAR.snippetOperations.snippet.service.SnippetService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RuleService
    @Autowired
    constructor(
        private val ruleRepository: RuleRepository,
        private val userRuleRepository: UserRuleRepository,
        private val snippetService: SnippetService,
        private val lintRequestProducer: LintRequestProducer,
        private val objectMapper: ObjectMapper,
    ) {
        private val logger: Logger = LoggerFactory.getLogger(RuleService::class.java)

        fun createUserDefaultRules(userId: String) {
            if (userRuleRepository.existsByUserId(userId)) {
                return
            }

            logger.info("Creating default rules for User($userId)")

            val rules = ruleRepository.findAll()

            val userRules = mutableListOf<UserRule>()

            rules.forEach {
                userRules.add(
                    UserRule(
                        userId = userId,
                        value = it.defaultValue,
                        active = false,
                        rule = it,
                    ),
                )
            }

            logger.info("Saving default rules for User($userId)")
            try {
                userRuleRepository.saveAll(userRules)
            } catch (e: Exception) {
                logger.error("Error saving default rules for User($userId)")
                return
            }
        }

        fun getUserRulesByType(
            userId: String,
            ruleType: RuleType,
        ): List<GetUserRuleDTO> {
            logger.info("Getting rules of type $ruleType for User($userId)")

            return userRuleRepository.findAllByUserIdAndRuleRuleType(userId, ruleType).map {
                GetUserRuleDTO(
                    id = it.id!!,
                    userId = it.userId,
                    name = it.rule.name,
                    active = it.active,
                    valueType = it.rule.valueType,
                    ruleType = it.rule.ruleType,
                    value = if (it.active) it.value else it.rule.defaultValue,
                )
            }
        }

        suspend fun updateUserRules(
            userId: String,
            userRules: List<UpdateUserRuleDTO>,
        ): List<GetUserRuleDTO> {
            logger.info("Updating rules for User($userId)")

            val rules: List<UserRule> =
                userRules.map {
                    val rule =
                        userRuleRepository.findById(it.id).orElseThrow {
                            logger.info("UserRule(${it.id}) not found")
                            NotFoundException("User Rule not found")
                        }

                    rule.active = it.active
                    rule.value = it.value

                    rule
                }

            userRuleRepository.saveAll(rules)
            logger.info("Rules updated for User($userId)")

            if (rules.any { it.rule.ruleType == RuleType.LINTING }) {
                logger.info("Linting rules updated for User($userId)")

                val userSnippets: List<UserSnippet> = snippetService.updateUserSnippetsStatus(userId, SnippetStatus.PENDING)
                publishLintEvents(userId, userSnippets.map { it.snippet.id!! })
            }

            return rules.map {
                GetUserRuleDTO(
                    id = it.id!!,
                    userId = it.userId,
                    name = it.rule.name,
                    active = it.active,
                    valueType = it.rule.valueType,
                    ruleType = it.rule.ruleType,
                    value = if (it.active) it.value else it.rule.defaultValue,
                )
            }
        }

        private suspend fun publishLintEvents(
            userId: String,
            snippetIds: List<String>,
        ) {
            val userRules = getUserRulesByType(userId, RuleType.LINTING)
            snippetIds.forEach {
                logger.info("Publishing lint request for Snippet($it) for User($userId)")
                lintRequestProducer.publishEvent(
                    objectMapper.writeValueAsString(
                        LintRequestEvent(
                            userId = userId,
                            snippetId = it,
                            linterRules = parseLintingRules(userRules),
                        ),
                    ),
                )
            }
        }

        fun parseLintingRules(rules: List<GetUserRuleDTO>): LinterRulesDTO {
            val result = LinterRulesDTO()

            rules.forEach {
                when (it.name) {
                    "printlnNoExpressionArguments" -> result.printlnNoExpressionArguments = it.value.toBoolean()
                    "identifierCasing" -> result.identifierCasing = it.value
                    "readInputNoExpressionArguments" -> result.readInputNoExpressionArguments = it.value.toBoolean()
                }
            }
            return result
        }

        fun parseFormattingRules(rules: List<GetUserRuleDTO>): FormatterRulesDTO {
            val result = FormatterRulesDTO()

            rules.forEach {
                when (it.name) {
                    "spaceBeforeColon" -> result.spaceBeforeColon = it.value.toInt()
                    "spaceAfterColon" -> result.spaceAfterColon = it.value.toInt()
                    "spacesInAssignSymbol" -> result.spacesInAssignSymbol = it.value.toInt()
                    "lineJumpBeforePrintln" -> result.lineJumpBeforePrintln = it.value.toInt()
                    "identationInsideConditionals" -> result.identationInsideConditionals = it.value.toInt()
                }
            }
            return result
        }
    }
