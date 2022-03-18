package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class School {
	private final int[] entrance;
	private final int[] diningRoom;
	private final boolean[] professorTable;
	private TowerType towerType;
	private int numTowers;
	private final List<Student> studentDiningRoom;
	private final List<Student> studentEntrance;
	private List<Professor> professors;

	public School(int numTower, TowerType towerType) {
		this.towerType = towerType;
		this.numTowers = numTower;
		this.entrance = new int[RealmType.values().length];
		this.diningRoom = new int[RealmType.values().length];
		this.professorTable = new boolean[RealmType.values().length];
		for(int i = 0; i < RealmType.values().length; i++)
			professorTable[i] = false;
		studentEntrance = new ArrayList<>();
		studentDiningRoom = new ArrayList<>();
		professors = new ArrayList<>();
	}

	public void insertEntrance(Student s) {
		studentEntrance.add(s);
		entrance[s.getStudentType().ordinal()] ++;
	}

	public void insertDiningRoom(Student s) {
		studentDiningRoom.add(s);
		diningRoom[s.getStudentType().ordinal()] ++;
	}

	public void insertProfessor(Professor p) {
		professorTable[p.getProfessorType().ordinal()] = true;
		professors.add(p);
	}

	public void sendTowerToIsland(Island island) {
		island.putTower(towerType);
		numTowers--;
	}

	public int getNumStudentsDiningRoom(RealmType r) {
		return diningRoom[r.ordinal()];
	}

	public boolean isProfessorPresent(RealmType r){
		return professorTable[r.ordinal()];
	}

	public TowerType getTowerType(){
		return null;
	}
}
