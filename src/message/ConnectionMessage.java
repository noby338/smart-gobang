package message;

import java.io.Serializable;

public class ConnectionMessage implements Message,Serializable {
    private String OID;
    private int randomNum;
    public ConnectionMessage(String OID,int randomNum) {
        this.OID = OID;
        this.randomNum = randomNum;
    }

    public int getRandomNum() {
        return randomNum;
    }

    public String getOID() {
        return OID;
    }
}
