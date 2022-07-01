package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * IslandGroup is an Island implementation for islands that are unified. The IslandGroup is implemented with the
 * composite pattern.
 *
 * @see it.polimi.ingsw.model.Island
 */
public class IslandGroup extends Island {
	private final List<Island> islands;
	private int numTowers;

	/**
	 * Constructs an IslandGroup that is built over the specified islands and have the specified value for the mother
	 * presence.
	 *
	 * @param motherNaturePresent true if mother nature is present on the island group, otherwise false
	 * @param islands the unified islands
	 */
	public IslandGroup(boolean motherNaturePresent, Island ... islands) {
		super(islands[0].getTowerType(), motherNaturePresent);
		this.islands = new ArrayList<>();
		numTowers = 0;
		for (Island i: islands) {
			this.islands.add(i);
			numTowers += i.getNumTowers();
		}
	}

	/**
	 * Returns the islands of the island group
	 *
	 * @return the islands of the island group
	 */
	public List<Island> getIslands() {
		return islands;
	}

	/**
	 * Insert a tower in the island group
	 *
	 * @param towerType the inserted tower
	 */
	@Override
	public void putTower(TowerType towerType) {
		for (Island i: islands)
			i.putTower(towerType);
		super.setTowerType(towerType);
	}

	/**
	 * Returns the students of the island group
	 *
	 * @return the students of the island group
	 */
	@Override
	public List<Student> getStudents(){
		List<Student> res = new ArrayList<>();
		for(Island i: islands)
			res.addAll(i.getStudents());
		return res;
	}

	/**
	 * Adds a student to the island group. The student will be added to a random single island of the island group
	 *
	 * @param s the student that will be added
	 */
	@Override
	public void addStudent(Student s){
		Random rnd = new Random();
		int islandToInsert = rnd.nextInt(islands.size());
		islands.get(islandToInsert).addStudent(s);
	}

	/**
	 * Returns the number of students of the island group of the specified type
	 *
	 * @param studentType the type of students
	 * @return the number of students of the island group of the specified type
	 */
	@Override
	public int getNumStudentsOfType (RealmType studentType) {
		return islands.stream()
				.map(i -> i.getNumStudentsOfType(studentType))
				.reduce(0, Integer::sum);
	}

	/**
	 * Returns the number of towers that the island group can take
	 *
	 * @return the number of towers that the island group can take
	 */
	@Override
	public int getNumTowers() {
		return numTowers;
	}
}
