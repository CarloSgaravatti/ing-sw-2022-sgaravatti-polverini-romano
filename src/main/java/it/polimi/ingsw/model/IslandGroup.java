package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalIslandGroupException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IslandGroup extends Island {
	private final List<Island> islands;

	public IslandGroup(Island ... islands) throws IllegalIslandGroupException {
		super(islands[0].getTowerType());
		this.islands = new ArrayList<>();
		for (Island i: islands) {
			if (i.getTowerType() == null || i.getTowerType() != getTowerType())
				throw new IllegalIslandGroupException();
			this.islands.add(i);
		}
	}

	@Override
	public int getInfluence (Player p) {
		int res = 0;
		for (Island i: islands)
			res += i.getInfluence(p);
		return res;
	}

	@Override
	public void putTower(TowerType towerType) {
		for (Island i: islands)
			i.putTower(towerType);
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
}
