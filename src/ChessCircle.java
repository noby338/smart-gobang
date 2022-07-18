import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;

public class ChessCircle extends Circle implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DropShadow DSCH;
    private static final RadialGradient GRADIENTBLACK;
    private static final RadialGradient GRADIENTWHITE;
    private static final double RADIUS = 15;//棋子的半径
    private static ArrayList<ChessCircle> circleArrayList;
    private String[] info = new String[3];
    private boolean isBlack;
    private int chessX;
    private int chessY;
    private double stepTime;
    private int count;

    public ChessCircle(Checkerboard checkerboard, int chessX, int chessY, boolean isBlack, int count, double stepTime) {
        super(chessX * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheX(),
                chessY * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheX(),
                RADIUS);
        super.setEffect(DSCH);
        this.chessX = chessX;
        this.chessY = chessY;
        this.isBlack = isBlack;
        if (isBlack) {
            super.setFill(GRADIENTBLACK);
        } else {
            super.setFill(GRADIENTWHITE);
        }
        this.stepTime = stepTime;
        this.count = count;
        updateInfo();
        ChessCircle.circleArrayList.add(this);
        checkerboard.getPane().getChildren().add(this);
    }

    public ChessCircle(Checkerboard checkerboard, double doubleX, double doubleY, boolean isBlack) {
        super(doubleX, doubleY, RADIUS);
        super.setEffect(DSCH);
        if (isBlack) {
            super.setFill(GRADIENTBLACK);
        } else {
            super.setFill(GRADIENTWHITE);
        }
        this.count = -1;
        checkerboard.getPane().getChildren().add(this);
    }

    public void updateInfo() {
        info[0] = "坐标      ：(" + chessX + "," + chessY + ")";
        info[1] = "步时      ：" + String.format("%.1f", stepTime) + "s";
        info[2] = "步数      ：" + (count + 1);
    }

    static {
        circleArrayList = new ArrayList<>();
        //region 棋子的阴影
        DSCH = new DropShadow();
        DSCH.setOffsetY(2.0);
        DSCH.setOffsetX(2.0);
        DSCH.setRadius(6);
        DSCH.setColor(new Color(0, 0, 0, 0.5));
        //endregion

        //region 黑白棋的渐变
        GRADIENTBLACK = new RadialGradient(
                270,
                0,
                0.3,
                0.3,
                0.8,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, new Color(0.6, 0.6, 0.6, 1)),
                new Stop(1, new Color(0.25, 0.25, 0.25, 1)));

        GRADIENTWHITE = new RadialGradient(
                270,
                0,
                0.3,
                0.3,
                1.5,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(1, new Color(0.55, 0.55, 0.55, 1)));
        //endregion
    }

    public int getCount() {
        return count;
    }

    public String[] getInfo() {
        return info;
    }
}
