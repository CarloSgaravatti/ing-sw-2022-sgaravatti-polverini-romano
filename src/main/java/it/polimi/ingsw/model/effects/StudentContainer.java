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

/**
 * StudentContainer contains students that are present on a character. The class is observed by the game in order to
 * automatically insert students on it when a student is picked from it.
 *
 * @see it.polimi.ingsw.model.modelObservables.StudentContainerObservable
 */
public class StudentContainer extends StudentContainerObservable {
    private final List<Student> students;
    private final int maxStudents;

    /**
     * Constructs a new empty StudentContainer that contains no students
     */
    public StudentContainer() {
        super();
        students = new ArrayList<>();
        maxStudents = 4;
    }

    /**
     * Constructs a new StudentContainer that can contain at maximum the specified number of students and that will be
     * observed by the specified ModelObserver
     *
     * @param maxStudents the maximum number of students that the container can contain
     * @param observer the observer of the class
     */
    public StudentContainer(int maxStudents, ModelObserver observer) {
        super();
        this.maxStudents = maxStudents;
        super.addObserver(observer);
        students = new ArrayList<>();
        initializeContainer();
    }

    /**
     * Constructs a new StudentContainer that can contain at maximum the specified number of students and that will
     * contain all the specified students (if they are lesser than the maximum number of students)
     *
     * @param maxStudents the maximum number of students that the container can contain
     * @param students the initial students in the container
     */
    public StudentContainer(int maxStudents, Student ... students) {
        super();
        this.maxStudents = maxStudents;
        this.students = new ArrayList<>();
        for (Student student : students) {
            insertStudent(student);
        }
    }

    /**
     * Returns the students that are present on the container
     *
     * @return the students that are present on the container
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Pick a student of the specified type from the container
     *
     * @param studentType the type of students that will be returned
     * @param notify true if the observer will be notified about the pick (so he can reinsert a student), otherwise false
     * @return a student of the specified type that was previously on the container
     * @throws StudentNotFoundException if there isn't such a student in the container
     */
    public Student pickStudent(RealmType studentType, boolean notify) throws StudentNotFoundException {
        Optional<Student> student = students.stream()
                .filter(s -> s.getStudentType() == studentType)
                .findAny();
        if (student.isEmpty()) throw new StudentNotFoundException();
        if (notify) notifyObservers(this);
        students.remove(student.get());
        return student.get();
    }

    /**
     * Insert the specified student in the container
     *
     * @param student the student that will be inserted
     */
    public void insertStudent(Student student) {
        students.add(student);
    }

    /**
     * Initialize the students of the container. The method will continuously notify the observer until the student size
     * is correct (until the maximum number of students is reached)
     */
    private void initializeContainer() {
        while (students.size() < maxStudents) {
            notifyObservers(this);
        }
    }
}
