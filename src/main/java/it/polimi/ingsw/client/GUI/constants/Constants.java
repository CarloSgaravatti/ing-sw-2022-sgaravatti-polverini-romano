package it.polimi.ingsw.client.GUI.constants;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.util.Map;

/**
 * Constants class provides all paths for images that need to be accessed by more than one classes, these are
 * tower images, wizard images and student images
 */
public class Constants {
    public static final Map<TowerType, String> towerImages = Map.of(TowerType.BLACK, "/images/black_tower.png",
            TowerType.WHITE, "/images/white_tower.png", TowerType.GREY, "/images/grey_tower.png");
    public static final Map<WizardType, String> wizardImages = Map.of(WizardType.values()[0], "/images/assistants/CarteTOT_back_1@3x.png",
            WizardType.values()[1], "/images/assistants/CarteTOT_back_11@3x.png",
            WizardType.values()[2], "/images/assistants/CarteTOT_back_21@3x.png",
            WizardType.values()[3], "/images/assistants/CarteTOT_back_31@3x.png");

    public static final Map<RealmType, String> studentsImages =
            Map.of(RealmType.YELLOW_GNOMES, "/images/students/student_yellow.png",
                    RealmType.BLUE_UNICORNS, "/images/students/student_blue.png",
                    RealmType.GREEN_FROGS, "/images/students/student_green.png",
                    RealmType.PINK_FAIRES, "/images/students/student_pink.png",
                    RealmType.RED_DRAGONS, "/images/students/student_red.png");
}
