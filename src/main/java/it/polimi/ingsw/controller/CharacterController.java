package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterController {
    private final Game game;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final Map<String, Object> characterInput = new HashMap<>();

    public CharacterController(Game game) {
        this.game = game;
    }

    public void addListener(String propertyName, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    public void handleCharacterAction(List<String> args, CharacterCard characterCard, Player activePlayer)
            throws IllegalCharacterActionRequestedException, NotEnoughCoinsException, IllegalArgumentException {
        characterInput.clear();
        if (activePlayer.getTurnEffect().isCharacterPlayed()) {
            listeners.firePropertyChange(new PropertyChangeEvent(activePlayer.getNickName(), "Error",
                    ErrorMessageType.CHARACTER_ALREADY_PLAYED, "You cannot play a character, you have already played one in this turn"));
            throw new IllegalArgumentException();
        }
        if (!checkCharacterInput(args, characterCard)) {
            throw new IllegalCharacterActionRequestedException(characterCard.getId());
        }
        int coinToGeneralSupply = characterCard.getPrice();
        if (!characterCard.isCoinPresent()) coinToGeneralSupply--;
        characterCard.playCard(activePlayer);
        activePlayer.getTurnEffect().setCharacterEffectConsumed(false); //TODO: delete
        if (characterCard.requiresInput()) characterCard.useEffect(characterInput);
        activePlayer.getTurnEffect().setCharacterPlayed(true);
        game.insertCoinsInGeneralSupply(coinToGeneralSupply);
    }

    public boolean checkCharacterInput(List<String> args, CharacterCard characterCard) throws IllegalArgumentException {
        if (characterCard.requiresInput() && args.isEmpty()) return false;
        return switch (characterCard.getId()) {
            case 1 -> args.size() == 2 && checkRealmType(args.get(0)) && checkIsland(args.get(1));
            case 2, 4, 6, 8 -> args.isEmpty();
            case 3, 5 -> args.size() == 1 && checkIsland(args.get(0));
            case 7 -> {
                int studentsToPick = Integer.parseInt(args.get(0));
                if (studentsToPick <= 0 || studentsToPick > 3) yield false;
                yield args.size() == 2 * studentsToPick + 1 && checkRealms(args.subList(1, studentsToPick + 1), "CharacterStudents")
                        && checkRealms(args.subList(studentsToPick + 1, (2 * studentsToPick) + 1), "EntranceStudents");
            }
            case 10 -> {
                int studentsToPick = Integer.parseInt(args.get(0));
                if (studentsToPick <= 0 || studentsToPick > 2) yield false;
                yield args.size() == 2 * studentsToPick + 1 && checkRealms(args.subList(1, studentsToPick + 1), "EntranceStudents")
                        && checkRealms(args.subList(studentsToPick + 1, (2 * studentsToPick) + 1), "DiningRoomStudents");
            }
            case 9, 11, 12 -> args.size() == 1 && checkRealmType(args.get(0));
            default -> throw new IllegalArgumentException();
        };
    }

    private boolean checkIsland(String islandId) {
        int islandIndex;
        try {
            islandIndex = Integer.parseInt(islandId);
        } catch (NumberFormatException e) {
            return false;
        }
        if (islandIndex < 0 || islandIndex >= game.getIslands().size()) return false;
        Island island = game.getIslands().get(islandIndex);
        characterInput.put("Island", island);
        return true;
    }

    private boolean checkRealmType(String abbreviation) {
        RealmType realmType;
        try {
            realmType = RealmType.getRealmByAbbreviation(abbreviation);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        characterInput.put("Student", realmType);
        return realmType != null;
    }

    private boolean checkRealms(List<String> realmsAbbreviations, String realmsSource) {
        RealmType[] realmTypes;
        try {
            realmTypes = RealmType.getRealmsByAbbreviations(realmsAbbreviations);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        characterInput.put(realmsSource, realmTypes);
        return true;
    }
}
