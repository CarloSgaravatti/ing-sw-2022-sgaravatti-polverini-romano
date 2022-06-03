package it.polimi.ingsw.client.GUI.constants;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.util.Map;

public class Constants {
    public static final Map<TowerType, String> towerImages = Map.of(TowerType.BLACK, "/images/black_tower.png",
            TowerType.WHITE, "/images/white_tower.png", TowerType.GREY, "/images/grey_tower.png");
    public static final Map<WizardType, String> wizardImages = Map.of(WizardType.values()[0], "/images/assistants/CarteTOT_back_1@3x.png",
            WizardType.values()[1], "/images/assistants/CarteTOT_back_11@3x.png",
            WizardType.values()[2], "/images/assistants/CarteTOT_back_21@3x.png",
            WizardType.values()[3], "/images/assistants/CarteTOT_back_31@3x.png");
}
