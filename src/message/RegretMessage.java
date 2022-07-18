package message;

import java.io.Serializable;

public class RegretMessage implements Message, Serializable {
    private String info;
    private int num;
    private boolean allowregret;

    public RegretMessage(String info, boolean allowregret ,int num) {
        this.info = info;
        this.allowregret = allowregret;
        this.num = num;
    }

    public RegretMessage(String info, boolean allowregret) {
        this.info = info;
        this.allowregret = allowregret;
    }


    public RegretMessage(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isAllowregret() {
        return allowregret;
    }

    public void setAllowregret(boolean allowregret) {
        this.allowregret = allowregret;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
