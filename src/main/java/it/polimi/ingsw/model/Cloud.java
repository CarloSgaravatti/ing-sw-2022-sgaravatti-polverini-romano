package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.StudentsNumberInCloudException;

public class Cloud {
	private boolean studentsPresents;
	private Student[] students;
	public static final int MAX_NUM_STUDENTS = 3;

	public Cloud() {
		students = new Student[MAX_NUM_STUDENTS];
	}

	public int getStudentsNumber() {
		return (students[0] == null) ? 0 : 3;
	}

	public Student[] pickStudents() throws EmptyCloudException {
		if (students[0] == null) throw new EmptyCloudException();
		students[0] = null;
		return students;
	}

	public void insertStudents(Student[] students) throws StudentsNumberInCloudException {
		if (students.length != 3) throw new StudentsNumberInCloudException();
		this.students = students;
	}

}
