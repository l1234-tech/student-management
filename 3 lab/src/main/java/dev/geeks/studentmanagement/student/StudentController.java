package dev.geeks.studentmanagement.student;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import dev.geeks.studentmanagement.student.dto.PageResponse;
import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import dev.geeks.studentmanagement.student.dto.StudentDto;
import dev.geeks.studentmanagement.student.dto.StudentSearchRequest;
import dev.geeks.studentmanagement.student.dto.StudentUpdateRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
// REST controller студентов. Здесь должны остаться endpoints, совместимые с API второй лабораторной.
public class StudentController {
    @PostMapping("/api/requests/search")
    public PageResponse<StudentDto> search(@RequestBody StudentSearchRequest request) {
        return service.list(request);
    }

    @GetMapping("/api/requests")
    public PageResponse<StudentDto> getStudents(@ModelAttribute StudentSearchRequest request,
            @org.springframework.web.bind.annotation.RequestParam(name="is_foreign", required=false) Boolean foreign) {
        return service.list(new StudentSearchRequest(request.ISU(), request.name(), request.group(),
            request.dormitory(), request.room(), foreign == null ? request.isForeign() : foreign,
            request.sort(), request.page(), request.size()));
    }

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }
    @PostMapping("/api/requests")
    public ResponseEntity<Map<String, StudentDto>> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {

        var student = service.create(request);

        return ResponseEntity
            .status(201)
            .body(Map.of("data", student));
    }
    @GetMapping("/api/requests/{isu}")
    public Map<String, StudentDto> getStudent(@PathVariable Integer isu) {
        return Map.of("data", service.getByIsu(isu));
    }

    @DeleteMapping("/api/requests/{isu}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer isu) {
        service.delete(isu);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/requests/{isu}")
    public Map<String, StudentDto> replaceStudent(
            @PathVariable Integer isu,
            @Valid @RequestBody StudentCreateRequest request) {

        return Map.of("data", service.replace(isu, request));
    }
    @PatchMapping("/api/requests/{isu}")
    public Map<String, StudentDto> patchStudent(
            @PathVariable Integer isu,
            @RequestBody StudentUpdateRequest request) {

        return Map.of("data", service.patch(isu, request));
    }


}
