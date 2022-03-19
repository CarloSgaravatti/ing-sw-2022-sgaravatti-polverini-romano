package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.SchoolWithoutTowersException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class School {
	private final int[] entrance;
	private final int[] diningRoom;
	private final boolean[] professorTable;
	private final TowerType towerType;
	private int numTowers;
	private final List<Student> studentDiningRoom;
	private final List<Student> studentEntrance;
	private final List<Professor> professors;

	public School (int numTower, TowerType towerType) {
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

	public void insertEntrance (Student ... students){
		studentEntrance.addAll(Arrays.asList(students));
		for(Student s: students)
			entrance[s.getStudentType().ordinal()] ++;
	}

	public void insertDiningRoom (Student s) {
		studentDiningRoom.add(s);
		diningRoom[s.getStudentType().ordinal()] ++;
	}

	public void insertProfessor (Professor p) {
		professorTable[p.getProfessorType().ordinal()] = true;
		professors.add(p);
	}

	public void removeProfessor (Professor p){
		professorTable[p.getProfessorType().ordinal()] = false;
		professors.remove(p);
	}

	public void sendTowerToIsland (Island island) throws SchoolWithoutTowersException {
		if (numTowers == 0) throw new SchoolWithoutTowersException();
		island.putTower(towerType);
		numTowers--;
	}

	public void sendStudentToIsland (Island island, RealmType studentType) throws StudentNotFoundException {
		Optional<Student> toEliminate = studentEntrance.stream()
				.filter(a -> a.getStudentType() == studentType)
				.findFirst();
		if (toEliminate.isEmpty()) throw new StudentNotFoundException();
		studentEntrance.remove(toEliminate.get());
		entrance[studentType.ordinal()] --;
		island.addStudent(toEliminate.get());
	}

	public int getNumStudentsDiningRoom (RealmType r) {
		return diningRoom[r.ordinal()];
	}

	public boolean isProfessorPresent (RealmType r){
		return professorTable[r.ordinal()];
	}

	public TowerType getTowerType (){
		return towerType;
	}

	public int getNumTowers() {
		return numTowers;
	}
}
