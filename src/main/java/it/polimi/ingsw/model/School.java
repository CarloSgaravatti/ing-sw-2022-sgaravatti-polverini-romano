package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.modelObservables.ProfessorPresenceObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class School represents a school that is assigned to a player. A school is composed
 * of four parts: the entrance, which contains a list of students that is ready to be
 * sent to an Island or in the dining room, the dining room, which contains a list of students
 * that determine if a professor is present or not in the professor table, the professor
 * table, that contains the professors that a player have, and the garrison, which contains
 * the towers that are present in the school
 */
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

	/**
	 * Constructs a school with no students in entrance or in dining room and no
	 * professors in the professor table
	 * @param numTower the initial number of towers in the school
	 * @param towerType the tower type associated to the school
	 * @see TowerType
	 */
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

	/**
	 * Insert all the specified students in the school entrance
	 * @param students the students to insert in entrance
	 */
	public void insertEntrance (Student ... students){
		studentEntrance.addAll(Arrays.asList(students));
		for(Student s: students)
			entrance[s.getStudentType().ordinal()] ++;
	}

	/**
	 * Method insertDiningRoom inserts a students in the dining room
	 * @param student the student to insert
	 * @return true if the player can take a coin, otherwise false
	 * @throws FullDiningRoomException if the dining room if full for the student's Realm Type
	 */
	//This method is useful because a character card can give the opportunity to put a student
	//directly in the dining room, without passing from the entrance.
	public boolean insertDiningRoom (Student student) throws FullDiningRoomException {
		if (diningRoom[student.getStudentType().ordinal()] >= MAX_STUDENTS_DINING_ROOM) {
			throw new FullDiningRoomException();
		}
		studentDiningRoom.add(student);
		diningRoom[student.getStudentType().ordinal()] ++;
		int newStudentsDiningRoom = diningRoom[student.getStudentType().ordinal()];
		notifyObservers(student.getStudentType());
		//maybe the return condition can be done better
		return (newStudentsDiningRoom == 3 || newStudentsDiningRoom == 6 || newStudentsDiningRoom == 9);
	}

	/**
	 * Method moveFromEntranceToDiningRoom pick a student from the entrance and puts it in the dining room
	 * @param studentType the Realm Type of the student to pick from the entrance
	 * @return true if the player can take a coin, otherwise false
	 * @throws StudentNotFoundException if the entrance doesn't have a student of specified Realm Type
	 * @throws FullDiningRoomException if the dining room if full for the student's Realm Type
	 */
	public boolean moveFromEntranceToDiningRoom (RealmType studentType) throws StudentNotFoundException,
			FullDiningRoomException {
		return insertDiningRoom(removeStudentEntrance(studentType));
	}

	/**
	 * Inserts a professor in the professor table
	 * @param professorType the Realm Type of the professor to insert
	 */
	public void insertProfessor (RealmType professorType) {
		professorTable[professorType.ordinal()] = true;
	}

	/**
	 * Removes a professor from the professor table
	 * @param professorType the Realm Type of the professor to remove
	 */
	public void removeProfessor (RealmType professorType){
		professorTable[professorType.ordinal()] = false;
	}

	/**
	 * Method sendTowerToIsland sends some towers from the garrison to the specified island. The number of
	 * towers that are sent depends on how many towers the island can take
	 * @param island the island in which the tower is sent
	 */
	public void sendTowerToIsland (Island island) /*throws SchoolWithoutTowersException*/ {
		int numTowersIsland = island.getNumTowers();
		//if (numTowers < numTowersIsland) throw new SchoolWithoutTowersException();
		island.putTower(towerType);
		numTowers -= numTowersIsland;
	}

	/**
	 * Method sendStudentToIsland sends a student from the entrance of the specified Realm Type
	 * to the specified island.
	 * @param island the island on which the student is sent
	 * @param studentType the Realm Type of the student to pick from the entrance
	 * @throws StudentNotFoundException if the entrance doesn't have a student from the specified type
	 */
	public void sendStudentToIsland (Island island, RealmType studentType) throws StudentNotFoundException {
		island.addStudent(removeStudentEntrance(studentType));
	}

	/**
	 * Method getNumStudentsDiningRoom returns the number of students presents in the dining room
	 * of the specified Realm Type
	 * @param realmType the Realm Type of the number of students to return
	 * @return the number of students of the specified Realm Type
	 */
	public int getNumStudentsDiningRoom (RealmType realmType) {
		return diningRoom[realmType.ordinal()];
	}

	/**
	 * Method isProfessor present verifies whether the professor of the specified Realm Type is present
	 * or not in the professor table
	 * @param realmType the Realm Type of the professor
	 * @return true if the professor is present, otherwise false
	 */
	public boolean isProfessorPresent (RealmType realmType){
		return professorTable[realmType.ordinal()];
	}

	/**
	 * Returns the Tower Type that is assigned to the school
	 * @return the Tower Type of the school
	 */
	public TowerType getTowerType (){
		return towerType;
	}

	/**
	 * Returns the number of towers that are currently in the school
	 * @return the number of towers
	 */
	public int getNumTowers() {
		return numTowers;
	}

	/**
	 * Puts some towers in the school, the number of towers depends on which island they came from
	 * (1 if the island is a SingleIsland, more than 1 if the island is an IslandGroup)
	 * @param numTowersToIns the number of towers to insert
	 */
	public void insertTower(int numTowersToIns) {
		this.numTowers += numTowersToIns;
	}

	/**
	 * Removes a student of the specified Realm Type from the entrance
	 * @param studentType the Realm Type of the student to remove
	 * @return the student that has been removed from the entrance
	 * @throws StudentNotFoundException if the entrance doesn't have any student of the specified type
	 */
	public Student removeStudentEntrance(RealmType studentType) throws StudentNotFoundException {
		Student toEliminate = remove(entrance, studentEntrance, studentType);
		studentEntrance.remove(toEliminate);
		entrance[studentType.ordinal()] --;
		return toEliminate;
	}

	/**
	 * Removes a student of the specified Realm Type from the dining room
	 * @param studentType the Realm Type of the student to remove
	 * @return the student that has been removed from the dining room
	 * @throws StudentNotFoundException if the dining room doesn't have any student of the specified type
	 */
	public Student removeFromDiningRoom (RealmType studentType) throws StudentNotFoundException {
		Student toEliminate = remove(diningRoom, studentDiningRoom, studentType);
		studentDiningRoom.remove(toEliminate);
		diningRoom[studentType.ordinal()] --;
		return toEliminate;
	}

	//For a better reuse of code
	private static Student remove(int[] studentsOfType, List<Student> studentsList, RealmType studentType)
			throws StudentNotFoundException {
		if (studentsOfType[studentType.ordinal()] == 0) throw new StudentNotFoundException();
		return studentsList.stream()
				.filter(a -> a.getStudentType() == studentType)
				.findFirst()
				.orElseThrow(); //this should never be thrown
	}

	/**
	 * Returns the number of students of the specified Realm Type that are present in the entrance
	 * @param studentType the Realm Type of the students
	 * @return the number of students in the entrance having the specified Realm Type
	 */
	public int getStudentsEntrance (RealmType studentType) {
		return entrance[studentType.ordinal()];
	}
}
