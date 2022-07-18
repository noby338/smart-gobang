package message;

import java.io.Serializable;

public class ChatMessage implements Message, Serializable {
    private String info;
    public ChatMessage(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
