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
import java.util.*;

/**
 * AssistantTab represent the assistant container in the tab pane of the gui main scene. The AssistantTab container the
 * FlowPane that container the assistants of the player represented by the client and all the images of the assistants.
 */
public class AssistantsTab {
    private final FlowPane assistantContainer;
    private final ModelView modelView;
    private final PropertyChangeSupport gui = new PropertyChangeSupport(this);
    private final Map<Integer, ImageView> assistantImageViews = new HashMap<>();
    private final Map<Integer, Image> assistantImages = new HashMap<>();
    private boolean isAssistantSelectable = false;

    /**
     * Constructs an AssistantTab that will use the specified flow pane to contain the images of the assistants
     *
     * @param assistantContainer the flow pane that will contain the assistants
     * @param modelView the model view of the client
     */
    public AssistantsTab(FlowPane assistantContainer, ModelView modelView) {
        this.assistantContainer = assistantContainer;
        this.modelView = modelView;
        for (int i = 1; i <= 10; i++) {
            Image image = new Image(Objects.requireNonNull(getClass()
                    .getResourceAsStream("/images/assistants/Assistant" + i + ".png")));
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

    /**
     * Sets the event handler that will handle events of the specified mouse event type when selecting an assistant image
     *
     * @param eventType the type of mouse event
     * @param handler the event handler that will handle assistants selection
     */
    public void setEventHandler(EventType<MouseEvent> eventType, EventHandler<MouseEvent> handler) {
        for (ImageView assistant: assistantImageViews.values()) {
            assistant.addEventHandler(eventType, handler);
        }
    }

    //TODO: check if it can be deleted
    public void addListener(PropertyChangeListener listener) {
        gui.addPropertyChangeListener(listener);
    }

    /**
     * Removes the assistant image that have the specified assistant id from the deck
     *
     * @param id the id of the assistant
     */
    public void removeAssistantFromDeck(int id) {
        ImageView imageView = assistantImageViews.remove(id);
        assistantContainer.getChildren().remove(imageView);
        assistantImages.remove(id);
    }

    /**
     * Returns true if the assistants are selectable, otherwise false
     *
     * @return true if the assistants are selectable, otherwise false
     */
    public boolean isAssistantSelectable() {
        return isAssistantSelectable;
    }

    /**
     * @param assistantSelectable true if the assistants can be selected, otherwise false
     */
    public void setAssistantSelectable(boolean assistantSelectable) {
        isAssistantSelectable = assistantSelectable;
    }

    /**
     * Updates the assistants of the assistant deck by removing the ones that aren't present in the model view that
     * is associated to this object
     */
    public void updateAssistants() {
        List<Integer> assistantsIds = new ArrayList<>(assistantImageViews.keySet());
        List<Integer> assistantsPresent = new ArrayList<>(modelView.getClientPlayerAssistants().keySet());
        for (Integer assistantId: assistantsIds) {
            if (!assistantsPresent.contains(assistantId)) {
                removeAssistantFromDeck(assistantId);
            }
        }
    }
}
