package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.modelObservables.ProfessorPresenceObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class School extends ProfessorPresenceObservable {
	private final int[] entrance;
	private final int[] diningRoom;
	private final boolean[] professorTable;
	private final TowerType towerType;
	private int numTowers;
	private final List<Student> studentDiningRoom;
	private final List<Student> studentEntrance;
	private final int MAX_STUDENTS_DINING_ROOM = 10;

	//TODO: add observer from game

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
		//TODO: pick students from bag to initialize school
	}

	public void insertEntrance (Student ... students){
		studentEntrance.addAll(Arrays.asList(students));
		for(Student s: students)
			entrance[s.getStudentType().ordinal()] ++;
	}

	//These two methods return true if the player puts a student in a place which gives him a coin.
	//The first method is useful because a character card can give the opportunity to put a student
	//directly in the dining room, without passing from the entrance.
	public boolean insertDiningRoom (Student s) throws FullDiningRoomException {
		if (diningRoom[s.getStudentType().ordinal()] >= MAX_STUDENTS_DINING_ROOM) {
			throw new FullDiningRoomException();
		}
		studentDiningRoom.add(s);
		diningRoom[s.getStudentType().ordinal()] ++;
		int newStudentsDiningRoom = diningRoom[s.getStudentType().ordinal()];
		notifyObservers(s.getStudentType());
		//maybe the return condition can be done better
		return (newStudentsDiningRoom == 3 || newStudentsDiningRoom == 6 || newStudentsDiningRoom == 9);
	}

	public boolean moveFromEntranceToDiningRoom (RealmType studentType) throws StudentNotFoundException, FullDiningRoomException {
		return insertDiningRoom(removeStudentEntrance(studentType));
	}

	public void insertProfessor (RealmType professorType) {
		professorTable[professorType.ordinal()] = true;
	}

	public void removeProfessor (RealmType professorType){
		professorTable[professorType.ordinal()] = false;
	}

	public void sendTowerToIsland (Island island) /*throws SchoolWithoutTowersException*/ {
		int numTowersIsland = island.getNumTowers();
		//if (numTowers < numTowersIsland) throw new SchoolWithoutTowersException();
		island.putTower(towerType);
		numTowers -= numTowersIsland;
	}

	public void sendStudentToIsland (Island island, RealmType studentType) throws StudentNotFoundException {
		island.addStudent(removeStudentEntrance(studentType));
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

	public void insertTower(int numTowersToIns) {
		this.numTowers += numTowersToIns;
	}

	public Student removeStudentEntrance(RealmType studentType) throws StudentNotFoundException {
		Student toEliminate = remove(entrance, studentEntrance, studentType);
		studentEntrance.remove(toEliminate);
		entrance[studentType.ordinal()] --;
		return toEliminate;
	}

	public Student removeFromDiningRoom (RealmType studentType) throws StudentNotFoundException {
		Student toEliminate = remove(diningRoom, studentDiningRoom, studentType);
		studentDiningRoom.remove(toEliminate);
		diningRoom[studentType.ordinal()] --;
		return toEliminate;
	}

	//For a better reuse of code
	private static Student remove(int[] studentsOfType, List<Student> studentsList, RealmType studentType) throws StudentNotFoundException {
		if (studentsOfType[studentType.ordinal()] == 0) throw new StudentNotFoundException();
		return studentsList.stream()
				.filter(a -> a.getStudentType() == studentType)
				.findFirst()
				.orElseThrow(); //this should never be thrown
	}
}
