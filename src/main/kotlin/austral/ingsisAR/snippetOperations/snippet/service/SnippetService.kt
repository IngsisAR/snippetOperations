package austral.ingsisAR.snippetOperations.snippet.service

import austral.ingsisAR.snippetOperations.integration.AssetService
import austral.ingsisAR.snippetOperations.integration.SnippetPermissionService
import austral.ingsisAR.snippetOperations.shared.exception.ConflictException
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetPermissionDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetPaginatedSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.GetSnippetWithStatusDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.ShareSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.dto.UpdateSnippetDTO
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import austral.ingsisAR.snippetOperations.snippet.model.entity.UserSnippet
import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAR.snippetOperations.snippet.repository.SnippetRepository
import austral.ingsisAR.snippetOperations.snippet.repository.UserSnippetRepository
import austral.ingsisAR.snippetOperations.user.service.UserService
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SnippetService
    @Autowired
    constructor(
        private val snippetRepository: SnippetRepository,
        private val userSnippetRepository: UserSnippetRepository,
        private val assetService: AssetService,
        private val permissionService: SnippetPermissionService,
        private val userService: UserService,
    ) {
        private val logger: Logger = LoggerFactory.getLogger(SnippetService::class.java)

        @Transactional
        fun createSnippet(
            snippet: CreateSnippetDTO,
            userId: String,
            token: String,
        ): GetSnippetDTO {
            logger.info(
                "Creating snippet for user $userId, (name: ${snippet.name}, language: ${snippet.language}, content: ${snippet.content})",
            )

            val newSnippet =
                snippetRepository.save(
                    Snippet(
                        name = snippet.name,
                        language = snippet.language,
                    ),
                )
            val snippetId: String = newSnippet.id!!
            logger.info("Snippet($snippetId) with name ${snippet.name} created for user $userId")

            logger.info("Saving Snippet($snippetId) content on asset service: ${snippet.content}")
            val response = assetService.saveSnippet(snippetId, snippet.content)

            if (response.statusCode.is2xxSuccessful) {
                logger.info("Snippet($snippetId) content saved in asset service")

                try {
                    logger.info("Creating Snippet($snippetId) OWNER permissions for User($userId)")
                    permissionService.createSnippetPermission(
                        CreateSnippetPermissionDTO(
                            snippetId = snippetId,
                            userId = userId,
                            permissionType = "OWNER",
                        ),
                        token,
                    )
                } catch (e: Exception) {
                    logger.error("Error creating Snippet($snippetId) OWNER permissions for User($userId)")
                    throw ConflictException("Error creating snippet permissions")
                }

                logger.info("Creating User($userId) Pending Snippet($snippetId) Status")
                createUserPendingSnippet(userId, newSnippet)

                return GetSnippetDTO(
                    id = snippetId,
                    name = newSnippet.name,
                    language = newSnippet.language,
                    content = snippet.content,
                    author = userService.getUserById(userId),
                )
            } else {
                logger.error("Error saving Snippet($snippetId) content on asset service: ${snippet.content}")
                throw ConflictException("Error saving Snippet content on asset service")
            }
        }

        fun getSnippets(
            userId: String,
            token: String,
            pageNumber: Int,
            pageSize: Int,
        ): GetPaginatedSnippetWithStatusDTO {
            logger.info("Getting snippets for User($userId)")

            logger.info("Getting snippet permissions for User($userId)")
            val permissions = permissionService.getAllSnippetPermissions(userId, token, pageNumber, pageSize)
            if (!permissions.statusCode.is2xxSuccessful) {
                logger.info("Error getting snippets permissions for User($userId)")
                throw ConflictException("Error getting snippets permissions")
            }
            val snippets =
                permissions.body!!.permissions.map {
                    val snippetEntity = snippetRepository.findById(it.snippetId)
                    if (snippetEntity.isEmpty) {
                        logger.info("Snippet(${it.snippetId}) not found")
                        throw NotFoundException("Snippet not found")
                    }

                    logger.info("Getting Snippet(${it.snippetId}) content from asset service")
                    val content = assetService.getSnippet(it.snippetId)
                    if (!content.statusCode.is2xxSuccessful) {
                        logger.info("Error getting Snippet(${it.snippetId}) content from asset service")
                        throw ConflictException("Error getting Snippet content")
                    }

                    logger.info("Getting Snippet(${it.snippetId}) status for User($userId)")
                    val status = userSnippetRepository.findFirstBySnippetIdAndUserId(it.snippetId, userId)
                    if (status == null) {
                        logger.info("Snippet(${it.snippetId}) status not found for User($userId)")
                        throw NotFoundException("Snippet status not found")
                    }

                    GetSnippetWithStatusDTO(
                        id = it.snippetId,
                        name = snippetEntity.get().name,
                        language = snippetEntity.get().language,
                        content = content.body!!,
                        status = status.status,
                        author = userService.getUserById(it.authorId),
                    )
                }.toMutableList()

            return GetPaginatedSnippetWithStatusDTO(snippets, permissions.body!!.total)
        }

        fun getSnippetById(
            snippetId: String,
            userId: String,
            token: String,
        ): GetSnippetDTO {
            logger.info("Getting Snippet($snippetId) for User($userId)")

            val snippet = snippetRepository.findById(snippetId)
            if (snippet.isEmpty) {
                logger.info("Snippet($snippetId) not found")
                throw NotFoundException("Snippet not found")
            }

            logger.info("Getting Snippet($snippetId) content from asset service")
            val content = assetService.getSnippet(snippetId)
            if (!content.statusCode.is2xxSuccessful) {
                logger.info("Error getting Snippet($snippetId) content from asset service")
                throw ConflictException("Error getting Snippet content")
            }

            logger.info("Getting Snippet($snippetId) author")
            val author = permissionService.getAuthorBySnippetId(snippetId, token)
            if (!author.statusCode.is2xxSuccessful) {
                logger.info("Error getting Snippet($snippetId) author")
                throw ConflictException("Error getting Snippet author")
            }

            return GetSnippetDTO(
                id = snippetId,
                name = snippet.get().name,
                content = content.body!!,
                language = snippet.get().language,
                author = userService.getUserById(author.body!!),
            )
        }

        fun shareSnippet(
            snippet: ShareSnippetDTO,
            token: String,
        ) {
            logger.info("Sharing Snippet(${snippet.snippetId} with User(${snippet.userId})")

            val snippetEntity = snippetRepository.findById(snippet.snippetId)
            if (snippetEntity.isEmpty) {
                logger.info("Snippet(${snippet.snippetId}) not found")
                throw NotFoundException("Snippet not found")
            }

            logger.info("Creating Snippet(${snippet.snippetId}) SHARED permissions for User(${snippet.userId})")
            val body =
                CreateSnippetPermissionDTO(
                    snippetId = snippet.snippetId,
                    userId = snippet.userId,
                    permissionType = "SHARED",
                )
            val permission = permissionService.createSnippetPermission(body, token)
            if (permission.statusCode.is2xxSuccessful) {
                logger.info("Creating User(${snippet.userId}) Pending Snippet(${snippet.snippetId}) Status")
                createUserPendingSnippet(snippet.userId, snippetEntity.get())
            } else {
                logger.info("Error creating Snippet(${snippet.snippetId}) SHARED permissions for User(${snippet.userId})")
                throw ConflictException("Error creating snippet permissions")
            }
        }

        @Transactional
        fun deleteSnippet(
            snippetId: String,
            token: String,
        ) {
            logger.info("Deleting Snippet($snippetId)")

            val snippet = snippetRepository.findById(snippetId)
            if (snippet.isEmpty) {
                logger.info("Snippet($snippetId) not found")
                throw NotFoundException("Snippet not found")
            }

            logger.info("Deleting Snippet($snippetId) content from asset service")
            val assetResponse = assetService.deleteSnippet(snippetId)
            if (!assetResponse.statusCode.is2xxSuccessful) {
                logger.info("Error deleting Snippet($snippetId) content from asset service")
                throw ConflictException("Error deleting Snippet content")
            }

            logger.info("Deleting Snippet($snippetId) permissions")
            val permissionResponse = permissionService.deleteSnippetPermissions(snippetId, token)
            if (!permissionResponse.statusCode.is2xxSuccessful) {
                logger.info("Error deleting Snippet($snippetId) permissions")
                throw ConflictException("Error deleting Snippet permissions")
            }

            logger.info("Deleting Snippet($snippetId) from database")
            snippetRepository.deleteById(snippetId)
        }

        fun updateSnippet(
            snippetId: String,
            snippet: UpdateSnippetDTO,
            token: String,
        ): GetSnippetDTO {
            logger.info("Updating Snippet($snippetId) with (content: ${snippet.content})")

            val snippetEntity = snippetRepository.findById(snippetId)
            if (snippetEntity.isEmpty) {
                logger.info("Snippet($snippetId) not found")
                throw NotFoundException("Snippet not found")
            }

            logger.info("Deleting Snippet($snippetId) content on asset service")
            val deleteResponse = assetService.deleteSnippet(snippetId)
            if (!deleteResponse.statusCode.is2xxSuccessful) {
                logger.error("Error deleting Snippet($snippetId) content on asset service")
                throw ConflictException("Error deleting Snippet content on asset service")
            }

            logger.info("Updating Snippet($snippetId) content on asset service: ${snippet.content}")
            val response = assetService.updateSnippet(snippetId, snippet.content)
            if (!response.statusCode.is2xxSuccessful) {
                logger.error("Error updating Snippet($snippetId) content on asset service: ${snippet.content}")
                throw ConflictException("Error updating Snippet content on asset service")
            }

            logger.info("Getting Snippet($snippetId) author")
            val author = permissionService.getAuthorBySnippetId(snippetId, token)
            if (!author.statusCode.is2xxSuccessful) {
                logger.info("Error getting Snippet($snippetId) author")
                throw ConflictException("Error getting Snippet author")
            }

            return GetSnippetDTO(
                id = snippetId,
                name = snippetEntity.get().name,
                language = snippetEntity.get().language,
                content = snippet.content,
                author = userService.getUserById(author.body!!),
            )
        }

        private fun createUserPendingSnippet(
            userId: String,
            snippet: Snippet,
        ) {
            logger.info("Creating User($userId) pending Snippet(${snippet.id})")
            userSnippetRepository.save(
                UserSnippet(
                    userId = userId,
                    snippet = snippet,
                    status = SnippetStatus.PENDING,
                ),
            )
        }

        fun updateUserSnippetsStatus(
            userId: String,
            status: SnippetStatus,
        ): List<UserSnippet> {
            logger.info("Updating User($userId) snippets status to $status")

            val snippets = userSnippetRepository.findAllByUserId(userId)
            snippets.forEach {
                it.status = status
            }

            userSnippetRepository.saveAll(snippets)
            logger.info("User($userId) snippets status updated to $status")

            return snippets
        }

        @Transactional
        fun updateUserSnippetStatusBySnippetId(
            userId: String,
            snippetId: String,
            status: SnippetStatus,
        ) {
            logger.info("Updating User($userId) Snippet($snippetId) status to $status")
            userSnippetRepository.findFirstBySnippetIdAndUserId(snippetId, userId)?.let {
                it.status = status
                userSnippetRepository.save(it)
            } ?: throw NotFoundException("User Snippet not found")
        }
    }
