// REST контроллер
// принимает HTTP запросы и передает их в сервисный слой , а затем возвращает ответ 

package dev.geeks.studentmanagement.student;

import dev.geeks.studentmanagement.student.dto.PageResponse;
import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import dev.geeks.studentmanagement.student.dto.StudentDto;
import dev.geeks.studentmanagement.student.dto.StudentSearchRequest;
import dev.geeks.studentmanagement.student.dto.StudentUpdateRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
  private final StudentService service;

  public StudentController(StudentService service) {
    this.service = service;
  }

  @PostMapping("/api/user/students/search")
  public PageResponse<StudentDto> search(@RequestBody StudentSearchRequest request) {
    return service.list(request);
  }

  @GetMapping("/api/user/students")
  public PageResponse<StudentDto> getStudents(
      @ModelAttribute StudentSearchRequest request,
      @RequestParam(name = "is_foreign", required = false) Boolean foreign) {
    return service.list(
        new StudentSearchRequest(
            request.ISU(),
            request.name(),
            request.group(),
            request.dormitory(),
            request.room(),
            foreign == null ? request.isForeign() : foreign,
            request.sort(),
            request.page(),
            request.size()));
  }

  @GetMapping("/api/user/students/{isu}")
  public Map<String, StudentDto> getStudent(@PathVariable Integer isu) {
    return Map.of("data", service.getByIsu(isu));
  }

  @PostMapping("/api/admin/students")
  public ResponseEntity<Map<String, StudentDto>> createStudent(
      @Valid @RequestBody StudentCreateRequest request) {
    var student = service.create(request);
    return ResponseEntity.status(201).body(Map.of("data", student));
  }

  @DeleteMapping("/api/admin/students/{isu}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Integer isu) {
    service.delete(isu);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/api/admin/students/{isu}")
  public Map<String, StudentDto> replaceStudent(
      @PathVariable Integer isu, @Valid @RequestBody StudentCreateRequest request) {
    return Map.of("data", service.replace(isu, request));
  }

  @PatchMapping("/api/admin/students/{isu}")
  public Map<String, StudentDto> patchStudent(
      @PathVariable Integer isu, @RequestBody StudentUpdateRequest request) {
    return Map.of("data", service.patch(isu, request));
  }
}
