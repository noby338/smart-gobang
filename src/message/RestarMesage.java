package message;

import java.io.Serializable;

public class RestarMesage implements Message,Serializable {
    private String info;
    private boolean allowRestat;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isAllowRestat() {
        return allowRestat;
    }

    public void setAllowRestat(boolean allowRestat) {
        this.allowRestat = allowRestat;
    }

    public RestarMesage(String info) {
        this.info = info;
    }

    public RestarMesage(String info, boolean allowRestat) {
        this.info = info;
        this.allowRestat = allowRestat;
    }
}
