package it.polimi.ingsw.listeners;

import java.util.EventListener;

//ModelListener receives an update which is a player move (ex. insert student in island, move mother nature...)
//and transform this update in a message that will be sent to the client
//Don't know if this will be an interface or an abstract class
public interface ModelListener extends EventListener {}
