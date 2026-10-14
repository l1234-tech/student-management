package dev.geeks.studentmanagement.config;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
public class PageController {
    private ResponseEntity<Resource> page(String name){return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(new ClassPathResource("templates/"+name+".html"));}
    @GetMapping("/") public ResponseEntity<Resource> index(){return page("index");}
    @GetMapping({"/add-student","/add-student/"}) public ResponseEntity<Resource> form(){return page("student_form");}
    @GetMapping({"/student","/student/"}) public ResponseEntity<Resource> student(){return page("student");}
    @GetMapping({"/login","/login/"}) public ResponseEntity<Resource> login(){return page("login");}
}
