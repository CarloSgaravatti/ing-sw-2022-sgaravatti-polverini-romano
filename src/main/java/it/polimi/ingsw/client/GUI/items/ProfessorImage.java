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

/**
 * ProfessorImage is a gui representation of a professor that has the shape of a hexagon filled with an image of a
 * professor.
 * @see javafx.scene.shape.Polygon
 */
public class ProfessorImage extends Polygon {
    private RealmType realmType;
    private final Map<RealmType, String> imagesPath =
            Map.of(RealmType.YELLOW_GNOMES, "/images/professors/teacher_yellow.png",
                    RealmType.BLUE_UNICORNS, "/images/professors/teacher_blue.png",
                    RealmType.GREEN_FROGS, "/images/professors/teacher_green.png",
                    RealmType.RED_DRAGONS, "/images/professors/teacher_red.png",
                    RealmType.PINK_FAIRES, "/images/professors/teacher_pink.png");

    /**
     * Constructs a new ProfessorImage. The new image will have the shape of a hexagon that have a circle inscribed with
     * specified radius and that will have the specified layoutX and layoutY. The image will be initially empty and not
     * visible in the scene.
     *
     * @param containedCircleRadius the radius of the inscribed circle
     * @param layoutX the layoutX of the professor
     * @param layoutY the layoutY of the professor
     */
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

    /**
     * Sets the value of the realm of the professor; this will fill the hexagon with the image of the professor, rotated
     * by 90 degrees.
     *
     * @param realmType the realm of the professor
     */
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
