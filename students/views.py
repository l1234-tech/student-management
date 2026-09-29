import json
 
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
 
from . import services
from .errors import ApiError, BadRequest


def index(request):
    return render(request , 'students/index.html')

def student_form(request):
    return render(request , 'students/student_form.html')

def error_response(status: int, code: str, message: str, details=None):
    payload = {"error": {"code": code, "message": message}}
    if details:
        payload["error"]["details"] = details
    return JsonResponse(payload, status=status, json_dumps_params={"ensure_ascii": False})

def _parse_json_body(request) -> dict:
    try:
        data = json.loads(request.body or b"{}")
    except json.JSONDecodeError:
        raise BadRequest("Тело запроса должно быть корректным JSON")
    if not isinstance(data, dict):
        raise BadRequest("Тело запроса должно быть JSON-объектом")
    return data

def api_requests(request):
    try:
        if request.method == "GET":
            students = services.list_students(request.GET.dict())
            return JsonResponse(students, safe=False, json_dumps_params={"ensure_ascii": False})

        if request.method == "QUERY":
            students = services.list_students(_parse_json_body(request))
            return JsonResponse(students, safe=False, json_dumps_params={"ensure_ascii": False})

        if request.method == "POST":
            student = services.create_student(_parse_json_body(request))
            return JsonResponse(student, status=201, json_dumps_params={"ensure_ascii": False})

        return error_response(405, "method_not_allowed", "Метод не поддерживается")
    except ApiError as e:
        return error_response(e.status, e.code, e.message, e.details)
    except Exception:
        return error_response(500, "internal_error", "Внутренняя ошибка сервера")

def api_request_detail(request, isu: str):
    try:
        isu_num = int(isu)
        if request.method == "GET":
            return JsonResponse(services.get_student(isu_num), json_dumps_params={"ensure_ascii": False})
        if request.method == "PATCH":
            student = services.update_student(isu_num, _parse_json_body(request))
            return JsonResponse(student, json_dumps_params={"ensure_ascii": False})
        if request.method == "DELETE":
            services.delete_student(isu_num)
            return HttpResponse(status=204)
        return error_response(405, "method_not_allowed", "Метод не поддерживается")
    except ApiError as e:
        return error_response(e.status, e.code, e.message, e.details)
    except ValueError:
        return error_response(400, "bad_request", "ИСУ в URL должен быть числом")
    except Exception:
        return error_response(500, "internal_error", "Внутренняя ошибка сервера")

def error_500(request):
    return error_response(500, "internal_error", "Внутренняя ошибка сервера")