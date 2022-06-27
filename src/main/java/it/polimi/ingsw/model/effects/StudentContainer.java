package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.modelObservables.StudentContainerObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentContainer extends StudentContainerObservable {
    private final List<Student> students;
    private final int maxStudents;

    //For testing
    public StudentContainer() {
        students = new ArrayList<>();
        maxStudents = 4;
    }

    public StudentContainer(int maxStudents, ModelObserver observer) {
        this.maxStudents = maxStudents;
        super.addObserver(observer);
        students = new ArrayList<>();
        initializeContainer();
    }

    public StudentContainer(int maxStudents, Student ... students) {
        this.maxStudents = maxStudents;
        this.students = new ArrayList<>();
        for (Student student : students) {
            insertStudent(student);
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student pickStudent(RealmType studentType, boolean notify) throws StudentNotFoundException {
        Optional<Student> student = students.stream()
                .filter(s -> s.getStudentType() == studentType)
                .findAny();
        if (student.isEmpty()) throw new StudentNotFoundException();
        if (notify) notifyObservers(this);
        students.remove(student.get());
        return student.get();
    }

    public void insertStudent(Student student) {
        students.add(student);
    }

    private void initializeContainer() {
        while (students.size() < maxStudents) {
            notifyObservers(this);
        }
    }
}
