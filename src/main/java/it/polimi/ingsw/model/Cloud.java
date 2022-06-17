package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.StudentsNumberInCloudException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Class Cloud represent a game cloud, that can contain some students (in base on how many
 * players have the game) that are picked from the cloud at the end of a round by players
 */
public class Cloud {
	private boolean studentsPresents;
	private Student[] students;
	private final PropertyChangeSupport game = new PropertyChangeSupport(this);

	/**
	 * Constructs a cloud with no students that can contain the specified number of students
	 * @param numStudents the number of students that cloud can contain
	 */
	public Cloud(int numStudents) {
		students = new Student[numStudents];
		studentsPresents = false;
	}

	/**
	 * Returns the number of students that the cloud actually contains, it can be 0 or the number of students
	 * form which the cloud was created
	 * @return the number of students in the cloud
	 */
	public int getStudentsNumber() {
		return (studentsPresents) ? students.length : 0;
	}

	/**
	 * Returns and removes students from the cloud
	 * @return the students that were present in the cloud before this method call
	 * @throws EmptyCloudException if there are no students in the cloud
	 */
	public Student[] pickStudents() throws EmptyCloudException {
		if (!studentsPresents) throw new EmptyCloudException();
		game.firePropertyChange("PickFromCloud", null, students);
		studentsPresents = false;
		return students;
	}

	/**
	 * Insert the specified students in the cloud, these students have to match the number of students that the cloud
	 * can contain
	 * @param students the students to insert in the cloud
	 * @throws StudentsNumberInCloudException if students length doesn't match the number of students that the cloud
	 * can contain
	 */
	public void insertStudents(Student ... students) throws StudentsNumberInCloudException {
		if (students.length != this.students.length) throw new StudentsNumberInCloudException();
		this.students = students;
		this.studentsPresents = true;
	}

	/**
	 * Return the students that are present in the cloud
	 * @return the students that are present in the cloud
	 */
	public Student[] getStudents() {
		return (studentsPresents) ? students : null;
	}

	/**
	 * Binds a PropertyChangeListener to the cloud. Events will be fired when a player pick students from the cloud
	 * @param listener the listener that will be added
	 */
	public void addListener(PropertyChangeListener listener) {
		game.addPropertyChangeListener(listener);
	}
}
