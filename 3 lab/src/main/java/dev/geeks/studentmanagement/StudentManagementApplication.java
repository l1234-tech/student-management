package dev.geeks.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Точка входа Spring Boot приложения. Здесь запускается backend третьей лабораторной.
@SpringBootApplication
public class StudentManagementApplication {
  public static void main(String[] args) {
    SpringApplication.run(StudentManagementApplication.class, args);
  }
}
