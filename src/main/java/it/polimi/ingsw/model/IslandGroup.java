package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class IslandGroup extends Island {
	private final List<Island> islands;

	public IslandGroup(Island i1, Island i2) {
		islands = new ArrayList<>();
		addIsland(i1);
		addIsland(i2);
	}

	public void addIsland(Island i) {
		islands.add(i);
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
			if (i.getTowerType() == null || i.getTowerType() != towerType)
				i.putTower(towerType);
	}

}
