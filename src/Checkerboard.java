import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * 棋盘
 */
public class Checkerboard {

    private static final int CHESIDELENGTH = 600;//棋盘的边长
    private static final int CHELINENUM = 15;//棋盘线的数量
    private static final int CHEMARGIN = 20;//线离棋盘边缘的距离
    private static final int CHEPADDING = (CHESIDELENGTH - 2 * CHEMARGIN) / (CHELINENUM - 1);//线之间的距离(40)
    private Pane pane;
    private int cheX;
    private int cheY;

    public Checkerboard(Pane pane, int cheX, int cheY) {
        //region 方形棋盘
        this.pane = pane;
        this.cheX = cheX;
        this.cheY = cheY;
        Rectangle checkerboard = new Rectangle();
        checkerboard.setX(cheX);
        checkerboard.setY(cheY);
        checkerboard.setWidth(CHESIDELENGTH);
        checkerboard.setHeight(CHESIDELENGTH);
        checkerboard.setArcHeight(20);
        checkerboard.setArcWidth(20);
        checkerboard.setFill(new Color(0.97, 0.91, 0.71, 1));
        //endregion

        //region 棋盘的阴影
        DropShadow dscb = new DropShadow();
        dscb.setOffsetY(7.0);
        dscb.setOffsetX(7.0);
        dscb.setRadius(20);
        dscb.setColor(new Color(0, 0, 0, 0.5));
        checkerboard.setEffect(dscb);
        pane.getChildren().add(checkerboard);
        //endregion

        //region 棋盘的线和点
        Color lineColor = new Color(0.75, 0.64, 0.47, 1);
        for (int i = 0; i < CHELINENUM; i++) {
            Line rowLine = new Line(cheX + CHEMARGIN, cheY + CHEMARGIN + CHEPADDING * i, cheX + CHEMARGIN + (CHELINENUM - 1) * CHEPADDING, cheY + CHEMARGIN + CHEPADDING * i);
            Line colLine = new Line(cheX + CHEMARGIN + CHEPADDING * i, cheY + CHEMARGIN, cheX + CHEMARGIN + CHEPADDING * i, cheY + CHEMARGIN + (CHELINENUM - 1) * CHEPADDING);
            rowLine.setStroke(lineColor);
            colLine.setStroke(lineColor);
            rowLine.setStrokeWidth(2.3);
            colLine.setStrokeWidth(2.3);
            pane.getChildren().addAll(rowLine, colLine);
        }

        pane.getChildren().addAll(
                new Circle(3 * CHEPADDING + cheX + CHEMARGIN, 3 * CHEPADDING + cheY + CHEMARGIN, 6, lineColor),
                new Circle(11 * CHEPADDING + cheX + CHEMARGIN, 3 * CHEPADDING + cheY + CHEMARGIN, 6, lineColor),
                new Circle(3 * CHEPADDING + cheX + CHEMARGIN, 11 * CHEPADDING + cheY + CHEMARGIN, 6, lineColor),
                new Circle(11 * CHEPADDING + cheX + CHEMARGIN, 11 * CHEPADDING + cheY + CHEMARGIN, 6, lineColor),
                new Circle(7 * CHEPADDING + cheX + CHEMARGIN, 7 * CHEPADDING + cheY + CHEMARGIN, 6, lineColor)
        );
        //endregion
    }

    public static int getCHESIDELENGTH() {
        return CHESIDELENGTH;
    }

    public static int getCHELINENUM() {
        return CHELINENUM;
    }

    public static int getCHEMARGIN() {
        return CHEMARGIN;
    }

    public static int getCHEPADDING() {
        return CHEPADDING;
    }

    public int getCheX() {
        return cheX;
    }

    public void setCheX(int cheX) {
        this.cheX = cheX;
    }

    public int getCheY() {
        return cheY;
    }

    public void setCheY(int cheY) {
        this.cheY = cheY;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

}
