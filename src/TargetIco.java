import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

public class TargetIco extends Polyline {
    private final static double icoLenth = 7;

    public TargetIco(Checkerboard checkerboard, boolean isBlack,int chessX, int chessY) {
        super();
        double crossX = chessX * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheX();
        double crossY = chessY * Checkerboard.getCHEPADDING() + Checkerboard.getCHEMARGIN() + checkerboard.getCheY();
        super.setStrokeWidth(3);
        super.getPoints().addAll(crossX, crossY,
                crossX -icoLenth, crossY,
                crossX, crossY,
                crossX, crossY +icoLenth,
                crossX, crossY,
                crossX +icoLenth, crossY,
                crossX, crossY,
                crossX, crossY -icoLenth,
                crossX, crossY);
        if (isBlack) {
            super.setStroke(new Color(1,1,1,0.6));
        }else {
            super.setStroke(new Color(0,0,0,0.5));
        }
        checkerboard.getPane().getChildren().add(this);
    }
}
