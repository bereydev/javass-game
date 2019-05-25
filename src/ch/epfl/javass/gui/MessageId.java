package ch.epfl.javass.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MessageId {
    
    HAPPY("/happy.png"),SAD("/sad.png"),ANGRY("/angry.png"),CHEEKY("/cheeky.png");
    
    private final String imagePath;
    private MessageId(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getImage() {
        return imagePath;
    }
    
    public static final List<MessageId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 4;
}
