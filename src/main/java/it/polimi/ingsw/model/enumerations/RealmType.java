package it.polimi.ingsw.model.enumerations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RealmType is an enumeration of all possible professors and students color. Each enum constant
 * is associated to an abbreviation.
 */
public enum RealmType {
    YELLOW_GNOMES("Y"),
    BLUE_UNICORNS("B"),
    GREEN_FROGS("G"),
    RED_DRAGONS("R"),
    PINK_FAIRES("P");

    private final String abbreviation;

    RealmType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Returns a string that is the abbreviation associated
     * to Realm Type on which this method is called
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns the RealmType enum constant that is associated to the specified abbreviation
     * @param abbreviation the abbreviation string of the RealmType that the method returns
     * @return the RealmType associated to the specified abbreviation
     * @throws ArrayIndexOutOfBoundsException if the abbreviation is not correct
     */
    public static RealmType getRealmByAbbreviation(String abbreviation) throws ArrayIndexOutOfBoundsException {
        int i = 0;
        while (!RealmType.values()[i].getAbbreviation().equals(abbreviation)) {
            i++;
        }
        return RealmType.values()[i];
    }

    /**
     * Returns all the RealmTypes that are associated to all the specified abbreviations
     * @param abbreviations the abbreviation strings of the RealmTypes that the method returns
     * @return an array of RealmTypes that contains in position i the RealmType that is associated
     * to the abbreviation in position i of the abbreviations list
     * @throws ArrayIndexOutOfBoundsException if one or more abbreviations are not correct
     */
    public static RealmType[] getRealmsByAbbreviations(List<String> abbreviations)
            throws ArrayIndexOutOfBoundsException {
        RealmType[] result = new RealmType[abbreviations.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRealmByAbbreviation(abbreviations.get(i));
        }
        return result;
    }

    /**
     * Return an <CODE>Integer[]</CODE> representation of the specified realm types. This representation consent an easier
     * management of students for firing events. The integer representation have the same length of the number of enum
     * constants of this class; also, <CODE>res[i]</CODE> is the number of elements of the specified array that are equal to
     * <CODE>RealmType.values()[i]</CODE>
     * @param realmTypes the student types that will be converted to an integer representation
     * @return the integer representation.
     */
    public static Integer[] getIntegerRepresentation(RealmType[] realmTypes) {
        Integer[] res = new Integer[values().length];
        Arrays.fill(res, 0);
        for (RealmType r: realmTypes) {
            res[r.ordinal()]++;
        }
        return res;
    }

    /**
     * Get an array of realm types from its integer representation. The result is ordered following the RealmType enum
     * constants order: for example, if <CODE>students[0] == 3</CODE> all elements from 0 to 2 of the resulting array will
     * be <CODE>RealmType.YELLOW_GNOMES</CODE>
     * @param students the integer representation
     * @return the array of RealmType that corresponds to the integer representation.
     */
    public static RealmType[] getRealmsFromIntegerRepresentation(Integer[] students) {
        List<RealmType> realmTypes = new ArrayList<>();
        for (int j = 0; j < students.length; j++) {
            for (int i = 0; i < students[j]; i++) {
                realmTypes.add(RealmType.values()[j]);
            }
        }
        return realmTypes.toArray(new RealmType[0]);
    }
}
