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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CharacterController handles a character action request by controlling if the input is correct and, if so, it
 * constructs the input that will be given to requested character in the model package and call the corresponding
 * method. In this way, characters methods are called only if the input is correct.
 */
public class CharacterController {
    private transient final Game game;
    private transient final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final Map<String, Object> characterInput = new HashMap<>();

    /**
     * Construct a new instance of the CharacterController that will be associated to the specified game.
     *
     * @param game the game on which the character controller will control characters requests
     */
    public CharacterController(Game game) {
        this.game = game;
    }

    /**
     * Adds a PropertyChangeListener to this object, which will listen the specified property name
     *
     * @param propertyName the name of the property
     * @param listener the listener that will listen to this
     */
    public void addListener(String propertyName, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Handles a character action request. The request is given as a list of String parameters for the specified
     * character card and is formulated by the specified player (which is the active player of the turn). The method first
     * control the input, then (if the input is ok) it transforms the input in a map that contains all the entries that
     * the specified character requires and at the end it calls the character action.
     *
     * @param args the arguments of the character request
     * @param characterCard the character that the player wants to play
     * @param activePlayer the player who wants to play the character
     * @throws IllegalCharacterActionRequestedException if the input is not correct or if the action encountered other
     *          type of errors that are character-specific
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to play the character
     * @throws IllegalArgumentException if the player has already played a character in the turn
     */
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

    /**
     * Controls if the input of the character request is correct
     *
     * @param args the parameters of the request
     * @param characterCard the characters on which the control is based
     * @return true if the input is correct, otherwise false
     * @throws IllegalArgumentException if the character has a non-valid id
     */
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

    /**
     * Check if the island contained in the request has a string exist in the game
     *
     * @param islandId the island, expressed as his id in a String
     * @return true if the id is valid, otherwise false
     */
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

    /**
     * Checks if the specified realm type, expressed by its abbreviation is valid
     *
     * @param abbreviation the realm type abbreviation
     * @return true if the abbreviation is valid, otherwise false
     */
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

    /**
     * Checks if the specified realm types, expressed by their abbreviation are valid. The specified realms source will
     * be used to distinguish inputs that require more than one RealmType[] (for example character 10 requires a RealmType[]
     * for the entrance and one for the dining room)
     *
     * @param realmsAbbreviations the realm types abbreviation
     * @param realmsSource the source of the realm types
     * @return true if the abbreviations are valid, otherwise false
     */
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
