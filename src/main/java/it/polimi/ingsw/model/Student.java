package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;

/**
 *
 */
public class Student {
	private final RealmType studentType;

	/**
	 * Constructs a student of the specified type
	 * @param studentType the RealmType of the student to construct
	 */
	public Student (RealmType studentType) {
		this.studentType = studentType;
	}

	/**
	 * Method getStudentType returns the RealmType associated to the student
	 * @return the RealmType of the student
	 */
	public RealmType getStudentType() {
		return studentType;
	}
}
