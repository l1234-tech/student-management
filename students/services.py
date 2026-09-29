import re
from datetime import date
 
from .errors import BadRequest, ConflictError, NotFound, ValidationError
from .repository import StudentRepository

FULL_NAME_RE = re.compile(r"^[A-Za-zА-Яа-яЁё\s]+$")
GROUP_RE = re.compile(r"^[A-Za-zА-Яа-яЁё]\d{4,5}$")
ISO_DATE_RE = re.compile(r"^\d{4}-\d{2}-\d{2}$")
 
FILTER_FIELDS = ("ISU", "group", "dormitory", "room", "is_foreign")
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
                raise ValueError(f"{key: Должно быть целым числом}")

        if key == "is_foreign":
            value = str(value).lower() in ("true", "1" , "yes")

        students = [s for s in students if s[key] == value]
    return students

def get_student(ISU:int) -> dict:
    student = repo.get_by_id(ISU)
    if student is None:
        raise NotFound(f"Студент с ИСУ {ISU} не найден")
    return student

def create_student(data: dict) -> dict:
    errors = _validate(data, partial = False)
    if errors:
        raise ValidationError(errors)

    if repo.get_by_id(data["ISU"]) is not None:
        raise ConflictError(f"Студент с ИСУ {data['isu']} уже существует")
    return repo.add(data)

def update_student(ISU: int, fields: dict) -> dict:
    if "ISU" in fields and fields["ISU"] != ISU:
        raise BadRequest("Поле isu изменять нельзя")
    errors = _validate(fields, partial=True)
    if errors:
        raise ValidationError(errors)
    student = repo.update(ISU, fields)
    if student is None:
        raise NotFound(f"Студент с ИСУ {ISU} не найден")
    return student

def delete_student(isu: int) -> None:
    if not repo.delete(isu):
        raise NotFound(f"Студент с ИСУ {isu} не найден")

def _validate(data: dict, partial: bool) -> dict:
    errors = {}
 
    if not partial:
        for field in ("ISU", "full_name", "group", "dormitory", "room", "checkin_date"):
            if field not in data:
                errors[field] = "Обязательное поле"
 
    if "full_name" in data and not FULL_NAME_RE.match(str(data["full_name"]).strip()):
        errors["full_name"] = "ФИО должно состоять только из букв"
    if "group" in data and not GROUP_RE.match(str(data["group"])):
        errors["group"] = "Группа должна начинаться с буквы, затем 4-5 цифр"
 
    for field, lo, hi in (("ISU", 100000, 999999), ("dormitory", 1, 20), ("room", 1000, 9999)):
        if field in data:
            value = data[field]
            if isinstance(value, bool) or not isinstance(value, int) or not lo <= value <= hi:
                errors[field] = f"Должно быть целым числом от {lo} до {hi}"
 
    if "checkin_date" in data:
        raw = str(data["checkin_date"])
        try:
            parsed = date.fromisoformat(raw)
        except ValueError:
            errors["checkin_date"] = "Формат даты: YYYY-MM-DD"
        else:
            if parsed.year < 2016:
                errors["checkin_date"] = "Вы не могли заселиться в такую дату, или вы уже не студент"
            elif parsed > date.today():
                errors["checkin_date"] = "Дата заселения не может быть в будущем"
 
    if "is_foreign" in data and not isinstance(data["is_foreign"], bool):
        errors["is_foreign"] = "Должно быть ДА или НЕТ"
    if "note" in data and not isinstance(data["note"], str):
        errors["note"] = "Должно быть строкой"
 
    return errors