package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.Student;

import java.util.List;

public interface StudentContainer {
    List<Student> getStudents();

    Student pickStudent(RealmType studentType) throws StudentNotFoundException;

    void insertStudent(Student student);
}
