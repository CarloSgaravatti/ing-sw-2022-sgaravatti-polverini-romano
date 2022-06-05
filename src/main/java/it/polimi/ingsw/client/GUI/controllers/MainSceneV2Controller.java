package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.GUI.items.AssistantsTab;
import it.polimi.ingsw.client.GUI.items.CloudImage;
import it.polimi.ingsw.client.GUI.items.SchoolBox;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.utils.Pair;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.*;

public class MainSceneV2Controller extends FXMLController implements Initializable {
    private ModelView modelView;
    private String clientNickname;
    @FXML
    private GridPane islandsGrid;
    @FXML
    private VBox cloudBox;
    @FXML
    private VBox playersBox;
    @FXML
    private AnchorPane clientSchoolPane;
    @FXML
    private AnchorPane secondPlayerSchoolPane;
    @FXML
    private AnchorPane thirdPlayerSchoolPane;
    @FXML
    private FlowPane assistantsPane;
    private AssistantsTab assistantsTab;
    private final Map<String, SchoolBox> playersSchools = new HashMap<>();
    private final EventHandler<MouseEvent> assistantEventHandler = mouseEvent -> {
        if (assistantsTab.isAssistantSelectable()) {
            ImageView assistantImage = (ImageView) mouseEvent.getTarget();
            String assistantId = assistantImage.getId().substring("Assistant".length());
            System.out.println("Selected assistant" + assistantId);
            firePropertyChange("PlayAssistant", null, assistantId);
            assistantsTab.setAssistantSelectable(false);
        } else {
            //TODO
        }
    };

    @FXML
    void onAccordionButtonPress(ActionEvent event) {
        Button button = (Button) event.getTarget();
        String buttonText = button.getText();
        double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
        Translate translation = new Translate();
        translation.setX(0);
        if (buttonText.equals("^")) {
            translation.setY(-yTranslation);
            button.setText("v");
        } else {
            translation.setY(yTranslation);
            button.setText("^");
        }
        playersBox.getTransforms().add(translation);
    }

    @Override
    public void onError(ErrorMessageType error) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void addListener(PropertyChangeListener gui) {
        super.addListener(gui);
        assistantsTab.addListener(gui);
    }

    public void initializeBoard(ModelView modelView, String clientNickname) {
        this.modelView = modelView;
        this.clientNickname = clientNickname;
        double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
        System.out.println(yTranslation);
        playersBox.setTranslateY(yTranslation);
        double cloudImageWidth = cloudBox.getWidth();
        for (int i = 0; i < modelView.getField().getCloudStudents().size(); i++) {
            cloudBox.getChildren().add(new CloudImage(i, modelView.getField(), cloudImageWidth));
        }
        /*TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        FlowPane assistantContainer = (FlowPane) ((AnchorPane) tabs.getTabs().get(0).getContent()).getChildren().get(0);*/
        assistantsTab = new AssistantsTab(assistantsPane, modelView);
        assistantsTab.setEventHandler(MouseEvent.MOUSE_CLICKED, assistantEventHandler);
        //AnchorPane clientSchoolPane = (AnchorPane) ((AnchorPane) tabs.getTabs().get(1).getContent()).getChildren().get(0);
        playersSchools.put(clientNickname, new SchoolBox(clientNickname, clientSchoolPane));
        List<String> otherPlayers = modelView.getPlayers().keySet().stream().filter(p -> !p.equals(clientNickname)).toList();
        playersSchools.put(otherPlayers.get(0), new SchoolBox(otherPlayers.get(0), secondPlayerSchoolPane));
        if (otherPlayers.size() == 2) {
            playersSchools.put(otherPlayers.get(1), new SchoolBox(otherPlayers.get(1), thirdPlayerSchoolPane));
        } else {
            TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
            tabs.getTabs().remove(3);
        }
    }

    public void onTurn(List<String> possibleActions) {
        if (possibleActions.contains("PlayAssistant")) assistantsTab.setAssistantSelectable(true);
    }

    public AssistantsTab getAssistantsTab() {
        return assistantsTab;
    }

    public void setAssistantImage(String player, int assistant) {
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/assistants/Assistant" + assistant + ".png")));
        playersSchools.get(player).setAssistantImage(image);
    }
}
