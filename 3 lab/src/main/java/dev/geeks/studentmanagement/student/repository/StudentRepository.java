package dev.geeks.studentmanagement.student.repository;

import dev.geeks.studentmanagement.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Repository студентов: CRUD, поиск по ISU, фильтрация, сортировка и пагинация через JPA.
public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
}
