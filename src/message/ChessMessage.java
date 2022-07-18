package message;

import java.io.Serializable;

public class ChessMessage implements Message,Serializable {
    private int chessX;
    private int chessY;

    public int getChessX() {
        return chessX;
    }

    public void setChessX(int chessX) {
        this.chessX = chessX;
    }

    public int getChessY() {
        return chessY;
    }

    public void setChessY(int chessY) {
        this.chessY = chessY;
    }

    public ChessMessage(int x, int y) {
        this.chessX = x;
        this.chessY = y;
    }

}
