from rest_framework.exceptions import APIException

class ApiError(APIException):
    status = 500
    code = "internal_error"

    def __init__(self, message: str, details: dict | None = None):
        self.message = message
        self.details = details or {}
        super().__init__(message)

class BadRequest(ApiError):
    status_code = 400
    default_code = "bad_request"

class NotFound(ApiError):
    status_code = 404
    default_code = "not_found"

class ConflictError(ApiError):
    status_code = 409
    default_code = "conflict"

class ValidationError(ApiError):
    status = 422
    code = "validation_error"
# вообще этот код ошибки указывает на неподходящую сущность , но такое название ошибки более явное
