package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CharacterControllerTest {
    private CharacterController characterController;
    private ActionControllerTest.GameControllerStub gameControllerStub;
    private Player activePlayer;

    private static class GameStub extends Game {
        public GameStub(GameConstants constants) {
            super(null, null, constants, true);
            super.createAllStudentsForBag();
        }

        //for these tests imagine that there are 6 islands
        @Override
        public List<Island> getIslands() {
            return new ArrayList<>(List.of(new SingleIsland(), new SingleIsland(), new SingleIsland(), new SingleIsland(),
                    new SingleIsland(), new SingleIsland()));
        }
    }

    @BeforeEach
    void setup() {
        gameControllerStub = new ActionControllerTest.GameControllerStub();
        try {
            gameControllerStub.setupGame();
        } catch (WizardTypeAlreadyTakenException | TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        characterController = gameControllerStub.getActionController().getCharacterController();
        activePlayer = gameControllerStub.getTurnController().getActivePlayer();
    }

    @RepeatedTest(5)
    void handleCharacterActionTest() {
        CharacterCard characterCard = gameControllerStub.getModel().getCharacterCards()[new Random().nextInt(3)];
        List<String> args = new ArrayList<>();
        if (characterCard.requiresInput()) {
            Assertions.assertThrows(IllegalCharacterActionRequestedException.class,
                    () -> characterController.handleCharacterAction(args, characterCard, activePlayer));
        } else {
            for (int i = 0; i < characterCard.getPrice(); i++) {
                activePlayer.insertCoin();
            }
            try {
                characterController.handleCharacterAction(args, characterCard, activePlayer);
            } catch (IllegalCharacterActionRequestedException | NotEnoughCoinsException e) {
                Assertions.fail();
            }
            Assertions.assertEquals(activePlayer, characterCard.getPlayerActive());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(it.polimi.ingsw.controller.CharacterInputArgumentProvider.class)
    void checkCharacterTest(List<String> args, int characterId, boolean expectedRes) {
        GameConstants constants = new GameConstants();
        constants.setNumCharacterPerGame(3);
        CharacterCard characterCard = new CharacterCreator(new GameStub(constants)).getCharacter(characterId);
        Assertions.assertEquals(expectedRes, characterController.checkCharacterInput(args, characterCard));
    }
}

class CharacterInputArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(List.of("Y"), 9, true),
                Arguments.of(List.of("Y"), 12, true),
                Arguments.of(List.of("3"), 9, false),
                Arguments.of(List.of("5"), 3, true),
                Arguments.of(List.of("2"), 5, true),
                Arguments.of(new ArrayList<>(), 2, true),
                Arguments.of(List.of("Y"), 4, false),
                Arguments.of(List.of("1", "Y", "B"), 10, true),
                Arguments.of(List.of("3", "B", "Y", "R", "P", "P", "G"), 9, false),
                Arguments.of(List.of("2", "T", "Y", "X", "P"), 9, false),
                Arguments.of(List.of("X"), 3, false)
        );
    }
}

