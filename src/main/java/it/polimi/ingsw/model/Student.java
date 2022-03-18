package it.polimi.ingsw.model;

public class Student {
	private final RealmType studentType;
	public static final int numStudents = 130;

	public Student (RealmType studentType){
		this.studentType = studentType;
	}

	//useless
	public void insertInCloud(Cloud c) {

	}

	//useless
	public void insertInIsland(Island chosenIsland) {

	}

	public RealmType getStudentType() {
		return studentType;
	}
}
