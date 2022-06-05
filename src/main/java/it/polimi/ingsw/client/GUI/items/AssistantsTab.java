package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ModelView;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssistantsTab {
    private final FlowPane assistantContainer;
    private final ModelView modelView;
    private final PropertyChangeSupport gui = new PropertyChangeSupport(this);
    private final Map<Integer, ImageView> assistantImageViews = new HashMap<>();
    private final Map<Integer, Image> assistantImages = new HashMap<>();
    private boolean isAssistantSelectable = false;

    public AssistantsTab(FlowPane assistantContainer, ModelView modelView) {
        this.assistantContainer = assistantContainer;
        this.modelView = modelView;
        for (int i = 0; i < 10; i++) {
            Image image = new Image(Objects.requireNonNull(getClass()
                    .getResourceAsStream("/images/assistants/Assistant" + (i + 1) + ".png")));
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitHeight((assistantContainer.getHeight() - assistantContainer.getVgap()) / 2);
            assistantContainer.getChildren().add(imageView);
            assistantImageViews.put(i, imageView);
            assistantImages.put(i, image);
            imageView.setId("Assistant" + i);
            imageView.getStyleClass().add("assistant-image");
        }
    }

    public void setEventHandler(EventType<MouseEvent> eventType, EventHandler<MouseEvent> handler) {
        for (ImageView assistant: assistantImageViews.values()) {
            assistant.addEventHandler(eventType, handler);
        }
    }

    public void addListener(PropertyChangeListener listener) {
        gui.addPropertyChangeListener(listener);
    }

    public void removeAssistantFromDeck(int id) {
        ImageView imageView = assistantImageViews.remove(id);
        assistantContainer.getChildren().remove(imageView);
        assistantImages.remove(id);
    }

    public boolean isAssistantSelectable() {
        return isAssistantSelectable;
    }

    public void setAssistantSelectable(boolean assistantSelectable) {
        isAssistantSelectable = assistantSelectable;
    }
}
