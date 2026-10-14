package dev.geeks.studentmanagement.student;

import dev.geeks.studentmanagement.student.dto.PageResponse;
import dev.geeks.studentmanagement.student.dto.StudentDto;
import dev.geeks.studentmanagement.student.dto.StudentSearchRequest;
import dev.geeks.studentmanagement.student.dto.StudentUpdateRequest;
import dev.geeks.studentmanagement.student.mapper.StudentMapper;
import dev.geeks.studentmanagement.student.model.Student;
import dev.geeks.studentmanagement.student.repository.StudentRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Validator;
import dev.geeks.studentmanagement.student.dto.StudentCreateRequest;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import java.time.LocalDate;
import dev.geeks.studentmanagement.common.exception.NotFoundException;
// Бизнес-логика студентов: CRUD, фильтрация, сортировка, пагинация, проверка уникальности ISU.
@Service

public class StudentService {
    private static final Set<String> SORT_FIELDS = Set.of(
            "ISU", "name", "group", "dormitory", "room", "checkinDate", "isForeign", "note");
    private final StudentRepository repository;
    private final Validator validator;

    public StudentService(StudentRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentDto> list(StudentSearchRequest request) {
        int page = request.page() == null ? 0 : request.page();
        int size = request.size() == null ? 20 : request.size();
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page >= 0, size от 1 до 100");
        }
        Sort sort = parseSort(request.sort());
        Specification<Student> filters = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (request.ISU() != null) predicates.add(cb.equal(root.get("isu"), request.ISU()));
            if (request.name() != null) predicates.add(cb.equal(root.get("name"), request.name()));
            if (request.group() != null) predicates.add(cb.equal(root.get("group"), request.group()));
            if (request.dormitory() != null) predicates.add(cb.equal(root.get("dormitory"), request.dormitory()));
            if (request.room() != null) predicates.add(cb.equal(root.get("room"), request.room()));
            if (request.isForeign() != null) predicates.add(cb.equal(root.get("isForeign"), request.isForeign()));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        var result = repository.findAll(filters, PageRequest.of(page, size, sort));
        return new PageResponse<>(result.getContent().stream().map(StudentMapper::toDto).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private Sort parseSort(String value) {
        if (value == null || value.isBlank()) return Sort.by("isu");
        String[] parts = value.split(",", -1);
        String field = parts[0].trim();
        if (parts.length > 2 || !SORT_FIELDS.contains(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field");
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2) {
            try {
                direction = Sort.Direction.fromString(parts[1].trim());
            } catch (IllegalArgumentException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort direction");
            }
        }
        String property = field.equals("ISU") ? "isu" : field;
        Sort sort = Sort.by(direction, property);
        return property.equals("isu") ? sort : sort.and(Sort.by("isu"));
    }

    @Transactional(readOnly = true)
    public StudentDto getByIsu(Integer isu) {
        var student = repository.findById(isu)
            .orElseThrow(() -> new NotFoundException("Студент не найден"));

        return StudentMapper.toDto(student);
    }

    @Transactional
    public void delete(Integer isu) {
        var student = repository.findById(isu)
            .orElseThrow(() -> new NotFoundException("Студент не найден"));

        repository.delete(student);
    }

    @Transactional
    public StudentDto create(StudentCreateRequest request) {
        LocalDate date;

        try {
            date = LocalDate.parse(request.checkinDate());
        } catch (java.time.format.DateTimeParseException exception) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Дата должна быть в формате YYYY-MM-DD"
            );
        }

        if (date.getYear() < 2016 || date.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Дата заселения должна быть с 2016 года и не в будущем"
            );
        }

        if (repository.existsById(request.ISU())) {
            throw new ConflictException("Студент с таким ИСУ уже существует");
        }

        var student = StudentMapper.fromCreateRequest(request);
        var savedStudent = repository.saveAndFlush(student);

        return StudentMapper.toDto(savedStudent);
    }
    @Transactional
    public StudentDto replace(Integer isu, StudentCreateRequest request) {
        var student = repository.findById(isu)
            .orElseThrow(() -> new NotFoundException("Студент не найден"));

        if (!isu.equals(request.ISU())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "ИСУ изменять нельзя"
            );
        }

        LocalDate date;

        try {
            date = LocalDate.parse(request.checkinDate());
        } catch (java.time.format.DateTimeParseException exception) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Дата должна быть в формате YYYY-MM-DD"
            );
        }

        if (date.getYear() < 2016 || date.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Дата заселения должна быть с 2016 года и не в будущем"
            );
        }

        student.setName(request.name());
        student.setGroup(request.group());
        student.setDormitory(request.dormitory());
        student.setRoom(request.room());
        student.setCheckinDate(date);
        student.setIsForeign(request.isForeign());
        student.setNote(request.note() == null ? "" : request.note());

        return StudentMapper.toDto(repository.saveAndFlush(student));
    }
    @Transactional
    public StudentDto patch(Integer isu, StudentUpdateRequest request) {
        var student = repository.findById(isu)
            .orElseThrow(() -> new NotFoundException("Студент не найден"));

        var merged = new StudentCreateRequest(
            isu,
            request.name() != null ? request.name() : student.getName(),
            request.group() != null ? request.group() : student.getGroup(),
            request.dormitory() != null
                ? request.dormitory() : student.getDormitory(),
            request.room() != null ? request.room() : student.getRoom(),
            request.checkinDate() != null
                ? request.checkinDate() : student.getCheckinDate().toString(),
            request.isForeign() != null
                ? request.isForeign() : student.getIsForeign(),
            request.note() != null ? request.note() : student.getNote()
        );

        var violations = validator.validate(merged);

        if (!violations.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                violations.iterator().next().getMessage()
            );
        }

        return replace(isu, merged);
    }
}
