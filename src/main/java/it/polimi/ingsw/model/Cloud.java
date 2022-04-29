package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.StudentsNumberInCloudException;

public class Cloud {
	private boolean studentsPresents;
	private Student[] students;

	public Cloud(int numStudents) {
		students = new Student[numStudents];
		studentsPresents = false;
	}

	public int getStudentsNumber() {
		return (studentsPresents) ? students.length : 0;
	}

	public Student[] pickStudents() throws EmptyCloudException {
		if (!studentsPresents) throw new EmptyCloudException();
		studentsPresents = false;
		return students;
	}

	public void insertStudents(Student ... students) throws StudentsNumberInCloudException {
		if (students.length != this.students.length) throw new StudentsNumberInCloudException();
		this.students = students;
		this.studentsPresents = true;
	}

	public Student[] getStudents() {
		return students;
	}
}
