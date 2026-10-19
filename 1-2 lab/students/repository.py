import json
import os
from pathlib import Path

DEFAULT_PATH = Path(__file__).resolve().parent.parent / "data" / "students_data.json"


class StudentRepository:
    def __init__(self, file_path: Path = DEFAULT_PATH):
        self.file_path = Path(file_path)

        if not file_path.exists():
            self.file_path.write_text("[]", encoding="utf-8")

    def get_all(self):
        data = self.file_path.read_text(encoding="utf-8")
        if not data.strip():
            return []
        return json.loads(data)

    def get_by_id(self, student_id: int):
        data = self.get_all()

        for student in data:
            if student.get("ISU") == student_id:
                return student

        return None

    def add(self, student: dict) -> bool:
        data = self.get_all()
        data.append(student)
        self.save(data)
        return student

    def update(self, student_id: int, fields: dict) -> dict | None:
        data = self.get_all()
        for i, student in enumerate(data):
            if student.get("ISU") == student_id:
                data[i] = {**student, **fields}
                self.save(data)
                return data[i]
        return None

    def delete(self, student_id: int) -> bool:
        data = self.get_all()

        if self.get_by_id(student_id):
            remaining = [
                student for student in data if student.get("ISU") != student_id
            ]
            self.save(remaining)
            return True

        return False

    def save(self, students: list[dict]) -> None:
        tmp = self.file_path.with_suffix(".tmp")
        tmp.write_text(
            json.dumps(students, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )
        os.replace(tmp, self.file_path)
