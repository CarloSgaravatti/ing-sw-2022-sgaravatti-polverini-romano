package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitControllerTest extends TestCase {
    InitController initController;
    GameController controller;

    @BeforeEach
    void setup() {
        controller = new GameController(1, 2, true);
        initController = controller.getInitController();
        try {
            initController.initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        controller.setGame();
        initController.addPlayer("player1");
        initController.addPlayer("player2");
    }

    @Test
    void setupPlayerTowerTest() {
        Player player = controller.getModel().getPlayerByNickname("player1");
        try {
            initController.setupPlayerTower(player, TowerType.BLACK);
        } catch (TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(controller.getModel().getGameConstants().getNumTowers(), player.getSchool().getNumTowers());
        Assertions.assertEquals(TowerType.BLACK, player.getSchool().getTowerType());
    }

    @Test
    void setupPlayerTowerExceptionTest() {
        Player player1 = controller.getModel().getPlayerByNickname("player1");
        Player player2 = controller.getModel().getPlayerByNickname("player2");
        try {
            initController.setupPlayerTower(player1, TowerType.BLACK);
        } catch (TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(TowerTypeAlreadyTakenException.class,
                () -> initController.setupPlayerTower(player2, TowerType.BLACK));
    }

    @Test
    void setupPlayerWizardTest() {
        Player player = controller.getModel().getPlayerByNickname("player1");
        try {
            initController.setupPlayerWizard(player, WizardType.values()[0]);
        } catch (WizardTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(WizardType.values()[0], player.getWizardType());
    }

    @Test
    void setupPlayerWizardExceptionTest() {
        Player player1 = controller.getModel().getPlayerByNickname("player1");
        Player player2 = controller.getModel().getPlayerByNickname("player2");
        try {
            initController.setupPlayerWizard(player1, WizardType.values()[0]);
        } catch (WizardTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(WizardTypeAlreadyTakenException.class,
                () -> initController.setupPlayerWizard(player2, WizardType.values()[0]));
    }

}