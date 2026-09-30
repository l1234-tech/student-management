from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework import status


def custom_exception_handler(exc, context):
    response = exception_handler(exc, context)

    if response is not None:
        code = _code_from_status(response.status_code)
        message = _extract_message(response.data)
        field = _extract_field(response.data)
        response.data = {'error': {'code': code, 'message': message}}
        if field:
            response.data['error']['field'] = field
        return response

    return Response(
        {'error': {'code': 'internal_error', 'message': 'Внутренняя ошибка сервера'}},
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
    if isinstance(data, dict) and len(data) == 1:
        return list(data.values())[0][0] if isinstance(list(data.values())[0], list) else list(data.values())[0]
    return str(data)


def _extract_field(data):
    if isinstance(data, dict) and len(data) == 1:
        return list(data.keys())[0]
    return None