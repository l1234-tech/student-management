package dev.geeks.studentmanagement;
import dev.geeks.studentmanagement.student.dto.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class StudentTextNormalizationTest {
    @Test void trimsCreationFieldsButKeepsSpacesBetweenWords() {
        var r=new StudentCreateRequest(123456,"  Маша Иванова  ","  P1234  ",5,1234," 2020-09-01 ",false,"  заметка  ");
        assertEquals("Маша Иванова",r.name());assertEquals("P1234",r.group());
        assertEquals("2020-09-01",r.checkinDate());assertEquals("заметка",r.note());
    }
    @Test void patchKeepsMissingFieldsAndTrimsProvidedValues() {
        var r=new StudentUpdateRequest(null,"  P1234  ",null,null,null,null,"   ");
        assertNull(r.name());assertNull(r.checkinDate());assertEquals("P1234",r.group());assertEquals("",r.note());
    }
    @Test void whitespaceOnlySearchIsAnEmptyCriterionNotAnAbsentOne() {
        var r=new StudentSearchRequest(null,"   ","\t ",null,null,null,null,null,null);
        assertEquals("",r.name());assertEquals("",r.group());
        var empty=new StudentSearchRequest(null,null,null,null,null,null,null,null,null);
        assertNull(empty.name());assertNull(empty.group());
    }
}
