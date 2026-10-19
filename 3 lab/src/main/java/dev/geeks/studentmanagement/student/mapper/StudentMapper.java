package dev.geeks.studentmanagement.student.mapper;

import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import dev.geeks.studentmanagement.student.dto.StudentDto;
import dev.geeks.studentmanagement.student.model.Student;
import java.time.LocalDate;

// Mapper между Student entity и DTO. Нужен, чтобы API не отдавал внутреннюю JPA-модель напрямую.
public class StudentMapper {
  public static StudentDto toDto(Student student) {
    return new StudentDto(
        student.getIsu(),
        student.getName(),
        student.getGroup(),
        student.getDormitory(),
        student.getRoom(),
        student.getCheckinDate() == null ? null : student.getCheckinDate().toString(),
        student.getIsForeign(),
        student.getNote());
  }

  public static Student fromCreateRequest(StudentCreateRequest request) {
    var student = new Student();

    student.setIsu(request.ISU());
    student.setName(request.name());
    student.setGroup(request.group());
    student.setDormitory(request.dormitory());
    student.setRoom(request.room());
    student.setCheckinDate(LocalDate.parse(request.checkinDate()));
    student.setIsForeign(request.isForeign());
    student.setNote(request.note());

    return student;
  }
}
