package ch.epfl.javass.gui;

import java.util.EnumMap;
import java.util.Map;

import ch.epfl.javass.jass.PlayerId;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MessageBean {

    private final Map<PlayerId, ObjectProperty<MessageId>> messages  = new EnumMap<>(PlayerId.class);
    
    public MessageBean( ) {
        for (PlayerId player : PlayerId.ALL)
            messages.put(player, new SimpleObjectProperty<MessageId>());
    }
    
    public void setMessage(PlayerId player, MessageId newMessage) {
        messages.get(player).set(newMessage);
    }
    
    public ReadOnlyObjectProperty<MessageId> messageProperty(PlayerId player){
        return messages.get(player);
    }
}
