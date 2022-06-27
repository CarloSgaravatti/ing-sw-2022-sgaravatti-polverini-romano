package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IslandGroup extends Island {
	private final List<Island> islands;
	private int numTowers;

	public IslandGroup(Island ... islands) /*throws IllegalIslandGroupException*/ {
		super(islands[0].getTowerType(), true);
		this.islands = new ArrayList<>();
		numTowers = 0;
		for (Island i: islands) {
			/*if (i.getTowerType() == null || i.getTowerType() != getTowerType())
				throw new IllegalIslandGroupException();*/
			this.islands.add(i);
			numTowers += i.getNumTowers();
		}
	}

	public List<Island> getIslands() {
		return islands;
	}

	@Override
	public void putTower(TowerType towerType) {
		for (Island i: islands)
			i.putTower(towerType);
		super.setTowerType(towerType);
	}

	@Override
	public List<Student> getStudents(){
		List<Student> res = new ArrayList<>();
		for(Island i: islands)
			res.addAll(i.getStudents());
		return res;
	}

	@Override
	public void addStudent(Student s){
		Random rnd = new Random();
		int islandToInsert = rnd.nextInt(islands.size());
		islands.get(islandToInsert).addStudent(s);
	}

	@Override
	public int getNumStudentsOfType (RealmType studentType) {
		return islands.stream()
				.map(i -> i.getNumStudentsOfType(studentType))
				.reduce(0, Integer::sum);
	}

	@Override
	public int getNumTowers() {
		return numTowers;
	}
}
