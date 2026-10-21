// Repository студентов: CRUD, поиск по ИСУ, фильтрация, сортировка и пагинация через JPA

package dev.geeks.studentmanagement.student.repository;

import dev.geeks.studentmanagement.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository
    extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {}
