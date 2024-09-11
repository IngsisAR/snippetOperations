package austral.ingsisAR.snippetOperations.testCase.service

import austral.ingsisAR.snippetOperations.integration.AssetService
import austral.ingsisAR.snippetOperations.integration.RunnerService
import austral.ingsisAR.snippetOperations.shared.exception.ConflictException
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import austral.ingsisAR.snippetOperations.snippet.repository.SnippetRepository
import austral.ingsisAR.snippetOperations.testCase.model.dto.BaseTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.CreateUpdateTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestCaseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.GetTestRunResponseDTO
import austral.ingsisAR.snippetOperations.testCase.model.dto.TestCaseEnvDTO
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCase
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseEnv
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseExpectedOutput
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseInput
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseEnvRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseExpectedOutputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseInputRepository
import austral.ingsisAR.snippetOperations.testCase.repository.TestCaseRepository
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestCaseService
    @Autowired
    constructor(
        private val testCaseRepository: TestCaseRepository,
        private val testCaseInputRepository: TestCaseInputRepository,
        private val testCaseExpectedOutputRepository: TestCaseExpectedOutputRepository,
        private val testCaseEnvRepository: TestCaseEnvRepository,
        private val snippetRepository: SnippetRepository,
        private val assetService: AssetService,
        private val runnerService: RunnerService,
    ) {
        private val logger: Logger = LoggerFactory.getLogger(TestCaseService::class.java)

        @Transactional
        fun createOrUpdateTestCase(testCase: CreateUpdateTestCaseDTO): GetTestCaseDTO {
            return if (testCase.id == null) {
                createTestCase(testCase)
            } else {
                updateTestCase(testCase)
            }
        }

        @Transactional
        fun createTestCase(testCase: CreateUpdateTestCaseDTO): GetTestCaseDTO {
            logger.info("Creating test case")

            val snippet: Snippet =
                snippetRepository.findById(testCase.snippetId).orElseThrow {
                    logger.info("Snippet(${testCase.snippetId}) not found")
                    NotFoundException("Snippet not found")
                }

            val newTestCase: TestCase =
                testCaseRepository.save(
                    TestCase(
                        name = testCase.name,
                        snippet = snippet,
                    ),
                )
            logger.info("Created TestCase(${newTestCase.id})")

            saveTestCaseParameters(testCase, newTestCase)

            return GetTestCaseDTO(
                id = newTestCase.id!!,
                snippetId = newTestCase.snippet.id!!,
                name = newTestCase.name,
                inputs = testCase.inputs,
                expectedOutputs = testCase.expectedOutputs,
                envs = testCase.envs,
            )
        }

        @Transactional
        fun updateTestCase(testCase: CreateUpdateTestCaseDTO): GetTestCaseDTO {
            logger.info("Updating TestCase(${testCase.id})")

            val testCaseEntity: TestCase =
                testCaseRepository.findById(testCase.id!!).orElseThrow {
                    logger.info("TestCase(${testCase.id}) not found")
                    NotFoundException("TestCase not found")
                }

            if (testCaseEntity.name != testCase.name) {
                testCaseEntity.name = testCase.name
                testCaseRepository.save(testCaseEntity)
                logger.info("Updated TestCase(${testCase.id})")
            }

            if (testCaseEntity.inputs.isNotEmpty()) {
                testCaseInputRepository.deleteAllById(testCaseEntity.inputs.map { it.id!! })
                logger.info("Deleted TestCase(${testCase.id}) inputs")
            }

            if (testCaseEntity.expectedOutputs.isNotEmpty()) {
                testCaseExpectedOutputRepository.deleteAllById(testCaseEntity.expectedOutputs.map { it.id!! })
                logger.info("Deleted TestCase(${testCase.id}) expected outputs")
            }

            if (testCaseEntity.envs.isNotEmpty()) {
                testCaseEnvRepository.deleteAllById(testCaseEntity.envs.map { it.id!! })
                logger.info("Deleted TestCase(${testCase.id}) envs")
            }

            saveTestCaseParameters(testCase, testCaseEntity)

            return GetTestCaseDTO(
                id = testCaseEntity.id!!,
                snippetId = testCaseEntity.snippet.id!!,
                name = testCaseEntity.name,
                inputs = testCase.inputs,
                expectedOutputs = testCase.expectedOutputs,
                envs = testCase.envs,
            )
        }

        fun getSnippetTestCases(snippetId: String): List<GetTestCaseDTO> {
            logger.info("Getting Snippet($snippetId) test cases")

            return testCaseRepository.findAllBySnippetId(snippetId).map {
                GetTestCaseDTO(
                    id = it.id!!,
                    snippetId = it.snippet.id!!,
                    name = it.name,
                    inputs = it.inputs.map { it.input },
                    expectedOutputs = it.expectedOutputs.map { it.output },
                    envs = it.envs.map { TestCaseEnvDTO(it.key, it.value) },
                )
            }
        }

        fun deleteTestCase(testCaseId: String) {
            logger.info("Deleting TestCase($testCaseId)")
            return testCaseRepository.deleteById(testCaseId)
        }

        fun runTestCase(
            testCaseId: String,
            token: String,
        ): GetTestRunResponseDTO {
            logger.info("Running TestCase($testCaseId)")

            val testCase: TestCase =
                testCaseRepository.findById(testCaseId).orElseThrow {
                    logger.info("TestCase($testCaseId) not found")
                    NotFoundException("TestCase not found")
                }

            logger.info("Getting Snippet(${testCase.snippet.id}) content")
            val snippetContent = assetService.getSnippet(testCase.snippet.id!!)
            if (!snippetContent.statusCode.is2xxSuccessful) {
                logger.info("Error getting Snippet(${testCase.snippet.id}) content from asset service")
                throw ConflictException("Error getting Snippet content")
            }

            val result =
                runnerService.runSnippet(
                    content = snippetContent.body!!,
                    inputs = testCase.inputs.map { it.input },
                    envs = testCase.envs.map { TestCaseEnvDTO(it.key, it.value) },
                    token = token,
                )

            if (!result.statusCode.is2xxSuccessful) {
                logger.info("Error running Snippet(${testCase.snippet.id})")
                throw ConflictException("Error running Snippet")
            }

            if (result.body!!.errors.isNotEmpty()) {
                logger.info("Snippet(${testCase.snippet.id}) failed")
                return GetTestRunResponseDTO(
                    passed = false,
                    message = result.body!!.errors.joinToString("\n"),
                )
            }

            val expectedOutputs = testCase.expectedOutputs.map { it.output }
            if (result.body!!.outputs != expectedOutputs) {
                logger.info(
                    "Snippet(${testCase.snippet.id}) failed, Expected outputs: $expectedOutputs\nActual outputs: ${result.body!!.outputs}",
                )
                return GetTestRunResponseDTO(
                    passed = false,
                    message = "Expected: $expectedOutputs\nActual: ${result.body!!.outputs}",
                )
            }

            logger.info("Snippet(${testCase.snippet.id}) Test passed")
            return GetTestRunResponseDTO(
                passed = true,
                message = "",
            )
        }

        private fun saveTestCaseParameters(
            testCase: BaseTestCaseDTO,
            newTestCase: TestCase,
        ) {
            testCaseInputRepository.saveAll(
                testCase.inputs.map {
                    TestCaseInput(
                        input = it,
                        testCase = newTestCase,
                    )
                },
            )
            logger.info("Created TestCase(${newTestCase.id}) inputs")

            testCaseExpectedOutputRepository.saveAll(
                testCase.expectedOutputs.map {
                    TestCaseExpectedOutput(
                        output = it,
                        testCase = newTestCase,
                    )
                },
            )
            logger.info("Created TestCase(${newTestCase.id}) expected outputs")

            testCaseEnvRepository.saveAll(
                testCase.envs.map {
                    TestCaseEnv(
                        key = it.key,
                        value = it.value,
                        testCase = newTestCase,
                    )
                },
            )
            logger.info("Created TestCase(${newTestCase.id}) envs")
        }
    }
