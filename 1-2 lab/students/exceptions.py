from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework import status
from rest_framework.exceptions import ValidationError as DRFValidationError


def custom_exception_handler(exc, context):
    response = exception_handler(exc, context)

    if response is not None:
        if isinstance(exc, DRFValidationError):
            response.status_code = status.HTTP_422_UNPROCESSABLE_ENTITY

        code = _code_from_status(response.status_code)

        response.data = {
            'error': {
                'code': code,
                'message': _extract_message(response.data),
                'details': response.data,
            }
        }
        return response

    return Response(
        {
            'error': {
                'code': 'internal_error',
                'message': 'Внутренняя ошибка сервера',
            }
        },
        status=status.HTTP_500_INTERNAL_SERVER_ERROR,
    )


def _code_from_status(status_code):
    return {
        400: 'bad_request',
        404: 'not_found',
        409: 'conflict',
        422: 'validation_error',
        500: 'internal_error',
    }.get(status_code, 'error')


def _extract_message(data):
    if isinstance(data, dict):
        first_value = next(iter(data.values()), None)

        if isinstance(first_value, list) and first_value:
            return str(first_value[0])

        if isinstance(first_value, dict):
            return "Ошибка валидации"

        if first_value is not None:
            return str(first_value)

    if isinstance(data, list) and data:
        return str(data[0])

    return str(data)