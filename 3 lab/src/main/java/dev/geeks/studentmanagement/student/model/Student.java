package dev.geeks.studentmanagement.student.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

// JPA entity студента. ISU должен быть уникальным идентификатором студента в API.
@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "isu", nullable = false)
    private Integer isu;
    private String name;
    @Column(name = "student_group")
    private String group;
    private Integer dormitory;
    private Integer room;
    private LocalDate checkinDate;
    private Boolean isForeign;
    @Column(length=2000)
    private String note;

    public Integer getIsu() { return isu; }
    public String getName() { return name; }
    public String getGroup() { return group; }
    public Integer getDormitory() { return dormitory; }
    public Integer getRoom() { return room; }
    public LocalDate getCheckinDate() { return checkinDate; }
    public Boolean getIsForeign() { return isForeign; }
    public String getNote() { return note; }

    public void setIsu(Integer isu) { this.isu = isu; }
    public void setName(String name) { this.name = name; }
    public void setGroup(String group) { this.group = group; }
    public void setDormitory(Integer dormitory) { this.dormitory = dormitory; }
    public void setRoom(Integer room) { this.room = room; }
    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }
    public void setIsForeign(Boolean isForeign) {
        this.isForeign = isForeign;
    }
    public void setNote(String note) { this.note = note; }
}
