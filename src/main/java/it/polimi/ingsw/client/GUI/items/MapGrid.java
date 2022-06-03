package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.FieldView;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class MapGrid {
    private GridPane grid;
    private FieldView field;
    private boolean isExpert;

    public MapGrid(GridPane grid, FieldView field, boolean isExpert) {
        this.grid = grid;
        this.field = field;
        this.isExpert = isExpert;
    }
}
