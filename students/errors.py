class ApiError(Exception):
    status = 500
    code = "internal_error"
 
    def __init__(self, message: str, details: dict | None = None):
        super().__init__(message)
        self.message = message
        self.details = details or {}

class BadRequest(ApiError):
    status = 400
    code = "bad_request"

class NotFound(ApiError):
    status = 404 
    code = "not_found"

class ConflictError(ApiError):
    status = 409
    code = "conflict"

class ValidationError(ApiError):
    status = 422
    code = "validation_errro" 
# вообще этот код ошибки указывает на неподходящую сущность , но такое название ошибки более явное