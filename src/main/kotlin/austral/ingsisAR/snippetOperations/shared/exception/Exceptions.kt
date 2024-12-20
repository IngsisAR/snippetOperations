package austral.ingsisAR.snippetOperations.shared.exception

import org.springframework.http.HttpStatus

open class HttpException(val status: HttpStatus, message: String) : RuntimeException(message)

class BadRequestException(message: String) : HttpException(HttpStatus.BAD_REQUEST, message)

class UnauthorizedException(message: String) : HttpException(HttpStatus.UNAUTHORIZED, message)

class NotFoundException(message: String) : HttpException(HttpStatus.NOT_FOUND, message)

class ConflictException(message: String) : HttpException(HttpStatus.CONFLICT, message)
