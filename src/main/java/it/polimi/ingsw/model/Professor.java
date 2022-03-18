package it.polimi.ingsw.model;

public class Professor {
	private final RealmType professorType;
	public static final int NUM_PROFESSORS = 5;

	public Professor (RealmType professorType){
		this.professorType = professorType;
	}

	public RealmType getProfessorType() {
		return professorType;
	}
}
