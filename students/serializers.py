import re
from datetime import date

from rest_framework import serializers


FULL_NAME_RE = re.compile(r"^[A-Za-zА-Яа-яЁё\s]+$")
GROUP_RE = re.compile(r"^[A-Za-zА-Яа-яЁё]\d{4,5}$")


class StudentSerializer(serializers.Serializer):

    ISU = serializers.IntegerField()
    name = serializers.CharField(max_length=200)
    group = serializers.CharField(max_length=20)
    dormitory = serializers.IntegerField()
    room = serializers.IntegerField()
    checkin_date = serializers.CharField(max_length=10)
    is_foreign = serializers.BooleanField()
    note = serializers.CharField(required=False, allow_blank=True)
    def validate_checkin_date(self, value):
        raw = str(value).strip()
        try:
            parsed = date.fromisoformat(raw)
        except ValueError:
            raise serializers.ValidationError("Формат даты: YYYY-MM-DD")

        if parsed.year < 2016:
            raise serializers.ValidationError(
                "Вы не могли заселиться в такую дату, или вы уже не студент"
            )
        if parsed > date.today():
            raise serializers.ValidationError(
                "Дата заселения не может быть в будущем"
            )
        return raw

    def validate(self, attrs):
        errors = {}
        if not self.partial:
            required = ("ISU", "name", "group",
                        "dormitory", "room", "checkin_date", "is_foreign")
            for field in required:
                if field not in attrs:
                    errors[field] = "Обязательное поле"

        if "name" in attrs:
            value = str(attrs["name"]).strip()
            if not FULL_NAME_RE.match(value):
                errors["name"] = "ФИО должно состоять только из букв"
            else:
                attrs["name"] = value

        if "group" in attrs:
            value = str(attrs["group"])
            if not GROUP_RE.match(value):
                errors["group"] = "Группа должна начинаться с буквы, затем 4-5 цифр"
        numeric_ranges = (
            ("ISU", 100000, 999999),
            ("dormitory", 1, 20),
            ("room", 1000, 9999),
        )
        for field, lo, hi in numeric_ranges:
            if field in attrs:
                value = attrs[field]
                if (isinstance(value, bool)
                        or not isinstance(value, int)
                        or not (lo <= value <= hi)):
                    errors[field] = f"Должно быть целым числом от {lo} до {hi}"

        if "checkin_date" in attrs:
            parsed = date.fromisoformat(attrs["checkin_date"])
            if parsed.year < 2016:
                errors["checkin_date"] = (
                    "Вы не могли заселиться в такую дату, или вы уже не студент"
                )
            elif parsed > date.today():
                errors["checkin_date"] = "Дата заселения не может быть в будущем"

        if "is_foreign" in attrs and not isinstance(attrs["is_foreign"], bool):
            errors["is_foreign"] = "Должно быть ДА или НЕТ"

        if "note" in attrs and not isinstance(attrs["note"], str):
            errors["note"] = "Должно быть строкой"

        if errors:
            raise serializers.ValidationError(errors)

        return attrs
    
    def validate_ISU(self, value):
        if isinstance(value, bool) or not isinstance(value, int):
            raise serializers.ValidationError("ИСУ должно быть целым числом")
        return value
