package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfessorImage extends Polygon {
    private RealmType realmType;
    private final Map<RealmType, String> imagesPath =
            Map.of(RealmType.YELLOW_GNOMES, "/images/professors/teacher_yellow.png",
                    RealmType.BLUE_UNICORNS, "/images/professors/teacher_blue.png",
                    RealmType.GREEN_FROGS, "/images/professors/teacher_green.png",
                    RealmType.RED_DRAGONS, "/images/professors/teacher_red.png",
                    RealmType.PINK_FAIRES, "/images/professors/teacher_pink.png");

    public ProfessorImage(double containedCircleRadius, double layoutX, double layoutY) {
        super();
        //is known only the distance of the two parallel lines
        List<Pair<Double, Double>> hexagonPoints = new ArrayList<>();
        double externalCircleRadius = containedCircleRadius / Math.cos(Math.PI / 6);
        //top point
        hexagonPoints.add(new Pair<>(0.0, externalCircleRadius));
        //right points
        hexagonPoints.addAll(List.of(new Pair<>(containedCircleRadius, externalCircleRadius / 2.0),
                new Pair<>(containedCircleRadius, - (externalCircleRadius / 2.0))));
        //bottom point
        hexagonPoints.add(new Pair<>(0.0, -externalCircleRadius));
        // left points
        hexagonPoints.addAll(List.of(new Pair<>(-containedCircleRadius, - (externalCircleRadius / 2.0)),
                new Pair<>(-containedCircleRadius, externalCircleRadius / 2.0)));
        super.getPoints().clear();
        List<Double> hexagon = new ArrayList<>();
        hexagonPoints.forEach(p -> {
            hexagon.add(p.getFirst());
            hexagon.add(p.getSecond());
        });
        super.getPoints().addAll(hexagon);
        super.setOpacity(0);
        super.setLayoutX(layoutX);
        super.setLayoutY(layoutY);
    }

    public void setRealmType(RealmType realmType) {
        this.realmType = realmType;
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagesPath.get(realmType))));
        ImageView iv = new ImageView(image);
        iv.setRotate(90);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        this.setFill(new ImagePattern(rotatedImage));
    }
}
