from .errors import BadRequest, ConflictError, NotFound
from .repository import StudentRepository
 
FILTER_FIELDS = ("ISU", "name", "group", "dormitory", "room", "is_foreign")
NUMERIC_FILTERS = ("ISU", "dormitory", "room")

repo = StudentRepository()


def list_students(filters: dict) -> list[dict]:
    students = repo.get_all()
    for key, value in filters.items():
        if key not in FILTER_FIELDS:
            raise BadRequest(f"Неизвестный параметр фильтра: {key}")
        if key in NUMERIC_FILTERS:
            try:
                value = int(value)
            except (TypeError, ValueError):
                raise BadRequest(f"{key}: должно быть целым числом")
        if key == "is_foreign":
            value = str(value).lower() in ("true", "1", "yes")
        students = [s for s in students if s.get(key) == value]
    return students


def get_student(ISU: int) -> dict:
    student = repo.get_by_id(ISU)
    if student is None:
        raise NotFound(f"Студент с ИСУ {ISU} не найден")
    return student


def find_student_by_isu(ISU: int) -> dict | None:
    return repo.get_by_id(ISU)


def create_student(data: dict) -> dict:
    if repo.get_by_id(int(data["ISU"])) is not None:
        raise ConflictError(f"Студент с ИСУ {data['ISU']} уже существует")
    return repo.add(data)


def update_student(ISU: int, fields: dict) -> dict:
    if "ISU" in fields and int(fields["ISU"]) != ISU:
        raise BadRequest("Поле ISU изменять нельзя")
    student = repo.update(ISU, fields)
    if student is None:
        raise NotFound(f"Студент с ИСУ {ISU} не найден")
    return student


def delete_student(isu: int) -> None:
    if not repo.delete(isu):
        raise NotFound(f"Студент с ИСУ {isu} не найден")