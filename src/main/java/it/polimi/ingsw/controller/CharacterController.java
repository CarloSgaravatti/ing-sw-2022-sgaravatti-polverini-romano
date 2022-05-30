package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class CharacterController {
    private final Game game;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public CharacterController(Game game) {
        this.game = game;
    }

    public void addListener(String propertyName, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    public void handleCharacterAction(List<String> args, CharacterCard characterCard, Player activePlayer)
            throws IllegalCharacterActionRequestedException, NotEnoughCoinsException {
        if (activePlayer.getTurnEffect().isCharacterPlayed()) {
            listeners.firePropertyChange("Error", ErrorMessageType.CHARACTER_ALREADY_PLAYED, activePlayer.getNickName());
            throw new IllegalArgumentException();
        }
        if (!checkCharacterInput(args, characterCard)) {
            listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, activePlayer.getNickName());
            throw new IllegalCharacterActionRequestedException();
        }
        int coinToGeneralSupply = characterCard.getPrice();
        if (!characterCard.isCoinPresent()) coinToGeneralSupply--;
        characterCard.playCard(activePlayer);
        activePlayer.getTurnEffect().setCharacterEffectConsumed(false);
        if (characterCard.requiresInput()) characterCard.useEffect(args);
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
                yield args.size() == 2 * studentsToPick + 1 && checkRealms(args.subList(1, studentsToPick + 1))
                        && checkRealms(args.subList(studentsToPick + 1, (2 * studentsToPick) + 1));
            }
            case 10 -> {
                int studentsToPick = Integer.parseInt(args.get(0));
                if (studentsToPick <= 0 || studentsToPick > 2) yield false;
                yield args.size() == 2 * studentsToPick + 1 && checkRealms(args.subList(1, studentsToPick + 1))
                        && checkRealms(args.subList(studentsToPick + 1, (2 * studentsToPick) + 1));
            }
            case 9, 11, 12 -> args.size() == 1 && checkRealmType(args.get(0));
            default -> throw new IllegalArgumentException();
        };
    }

    private boolean checkIsland(String islandId) {
        int island;
        try {
            island = Integer.parseInt(islandId);
        } catch (NumberFormatException e) {
            return false;
        }
        if (island < 0 || island >= game.getIslands().size()) return false;
        return true;
    }

    private boolean checkRealmType(String abbreviation) {
        RealmType realmType;
        try {
            realmType = RealmType.getRealmByAbbreviation(abbreviation);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return realmType != null;
    }

    private boolean checkRealms(List<String> realmsAbbreviations) {
        RealmType[] realmTypes;
        try {
            realmTypes = RealmType.getRealmsByAbbreviations(realmsAbbreviations);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }
}
