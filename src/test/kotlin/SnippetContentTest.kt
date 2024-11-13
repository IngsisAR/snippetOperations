import austral.ingsisAR.snippetOperations.run.model.dto.SnippetContent
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SnippetContentTest {
    private val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
    private val validator: Validator = validatorFactory.validator

    @Test
    fun `should validate SnippetContent with valid fields`() {
        val snippetContent = SnippetContent(content = "println('Hello, World!')", language = "Kotlin")
        val violations: Set<ConstraintViolation<SnippetContent>> = validator.validate(snippetContent)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `should not validate SnippetContent with blank content`() {
        val snippetContent = SnippetContent(content = "", language = "Kotlin")
        val violations: Set<ConstraintViolation<SnippetContent>> = validator.validate(snippetContent)
        assertFalse(violations.isEmpty())
        assertEquals("Content can't be blank", violations.first().message)
    }

    @Test
    fun `should not validate SnippetContent with blank language`() {
        val snippetContent = SnippetContent(content = "println('Hello, World!')", language = "")
        val violations: Set<ConstraintViolation<SnippetContent>> = validator.validate(snippetContent)
        assertFalse(violations.isEmpty())
        assertEquals("Language can't be blank", violations.first().message)
    }
}
