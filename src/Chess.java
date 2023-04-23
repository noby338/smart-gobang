import javafx.application.Platform;
import javafx.scene.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * 棋子
 */
public class Chess implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient Checkerboard checkerboard;
    private transient static ArrayList<Chess> chessArrayList;
    private final int chessX;
    private final int chessY;
    private static long initialMillis;
    private final ChessCircle chessCircle;
    private final long currentMillis;
    private final double stepTime;
    private final boolean isBlack;
    private final int count;//从0开始

    public void removeChessCircle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                checkerboard.getPane().getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        if (node instanceof ChessCircle) {
                            return ((ChessCircle) node).getCount() == count;
                        }
                        return false;
                    }
                });
            }
        });
    }

    public Chess(Checkerboard checkerboard, ArrayList<Chess> chessArrayList, int chessX, int chessY) {
        this.checkerboard = checkerboard;
        Chess.chessArrayList = chessArrayList;
        this.chessX = chessX;
        this.chessY = chessY;
        this.count = chessArrayList.size();
        this.isBlack = chessArrayList.size() % 2 == 0;
        chessArrayList.add(this);
        this.currentMillis = System.currentTimeMillis();
        this.stepTime = count == 0 ?  (currentMillis - initialMillis)  / 1000.0 :  (currentMillis - chessArrayList.get(count - 1).getCurrentMillis()) / 1000.0;
        this.chessCircle = new ChessCircle(checkerboard, chessX, chessY, isBlack, count ,stepTime);
    }


    public static ArrayList<Chess> getChessArrayList() {
        return chessArrayList;
    }

    public int getChessX() {
        return chessX;
    }

    public int getChessY() {
        return chessY;
    }

    public long getCurrentMillis() {
        return currentMillis;
    }

    public static void setInitialMillis(long initialMillis) {
        Chess.initialMillis = initialMillis;
    }

    public double getStepTime() {
        return stepTime;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public int getCount() {
        return count;
    }

    public ChessCircle getChessCircle() {
        return chessCircle;
    }


    @Override
    public String toString() {
        return "Chess{" +
                ", chessX=" + chessX +
                ", chessY=" + chessY +
                ", currentTime=" + currentMillis +
                ", stepTime=" + stepTime +
                ", isBlack=" + isBlack +
                ", count=" + count +
                '}';
    }
}

