package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimpleModel implements Serializable {
    private SimpleField field;
    private List<SimplePlayer> schools;
    private Map<String, TowerType> towers;
    private Map<String, WizardType> wizards;
    private String[] professorOwners;

    public SimpleModel(){
        super();
    }

    public SimpleModel(Map<String, TowerType> towers, Map<String, WizardType> wizards) {
        this();
        this.towers = towers;
        this.wizards = wizards;
    }

    public SimpleField getField() {
        return field;
    }

    public void setField(SimpleField field) {
        this.field = field;
    }

    public List<SimplePlayer> getSchools() {
        return schools;
    }

    public void setSchools(List<SimplePlayer> schools) {
        this.schools = schools;
    }

    public Map<String, TowerType> getTowers() {
        return towers;
    }

    public void setTowers(Map<String, TowerType> towers) {
        this.towers = towers;
    }

    public Map<String, WizardType> getWizards() {
        return wizards;
    }

    public void setWizards(Map<String, WizardType> wizards) {
        this.wizards = wizards;
    }

    public String[] getProfessorOwners() {
        return professorOwners;
    }

    public void setProfessorOwners(String[] professorOwners) {
        this.professorOwners = professorOwners;
    }
}
