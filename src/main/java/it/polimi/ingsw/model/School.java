package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.SchoolWithoutTowersException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.modelObservables.ProfessorPresenceObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class School extends ProfessorPresenceObservable {
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
		//Pick students from bag?
	}

	public void insertEntrance (Student ... students){
		studentEntrance.addAll(Arrays.asList(students));
		for(Student s: students)
			entrance[s.getStudentType().ordinal()] ++;
	}

	//These two methods return true if the player puts a student in a place which gives him a coin.
	//The first method is useful because a character card can give the opportunity to put a student
	//directly in the dining room, without passing from the entrance.
	public boolean insertDiningRoom (Student s) {
		studentDiningRoom.add(s);
		diningRoom[s.getStudentType().ordinal()] ++;
		int newStudentsDiningRoom = diningRoom[s.getStudentType().ordinal()];
		//maybe the return condition can be done better
		return (newStudentsDiningRoom == 3 || newStudentsDiningRoom == 6 || newStudentsDiningRoom == 9);
	}

	public boolean moveFromEntranceToDiningRoom (RealmType studentType) throws StudentNotFoundException {
		boolean res = insertDiningRoom(removeStudentEntrance(studentType));
		notifyObservers(studentType);
		return res;
	}

	public void insertProfessor (RealmType professorType) {
		professorTable[professorType.ordinal()] = true;
	}

	public void removeProfessor (RealmType professorType){
		professorTable[professorType.ordinal()] = false;
	}

	public void sendTowerToIsland (Island island) /*throws SchoolWithoutTowersException*/ {
		//if (numTowers == 0) throw new SchoolWithoutTowersException();
		island.putTower(towerType);
		numTowers--;
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

	public void insertTower() {
		numTowers++;
	}

	//This method is used when a student is moved from entrance to dining room or island
	private Student removeStudentEntrance(RealmType studentType) throws StudentNotFoundException{
		Optional<Student> toEliminate = studentEntrance.stream()
				.filter(a -> a.getStudentType() == studentType)
				.findFirst();
		if (toEliminate.isEmpty()) throw new StudentNotFoundException();
		studentEntrance.remove(toEliminate.get());
		entrance[studentType.ordinal()] --;
		return toEliminate.get();
	}
}
