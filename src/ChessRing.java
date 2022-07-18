import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class ChessRing extends Circle {
    private static final double RADIUS = 15;//棋子的半径

    public ChessRing(Checkerboard checkerboard, double chessX, double chessY) {
        super(chessX * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheX(),
                chessY * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheY(),
                RADIUS + 2);
        super.setFill(Color.rgb(0,0,0,0));
        super.setStroke(Color.rgb(150,255,0,0.8));
        super.setStrokeWidth(3);
        checkerboard.getPane().getChildren().add(this);
    }
}
