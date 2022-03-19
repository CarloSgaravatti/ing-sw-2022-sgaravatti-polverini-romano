package it.polimi.ingsw.model;

public class Student {
	private final RealmType studentType;
	public static final int NUM_STUDENTS = 130;

	public Student (RealmType studentType) {
		this.studentType = studentType;
	}

	public RealmType getStudentType() {
		return studentType;
	}
}
