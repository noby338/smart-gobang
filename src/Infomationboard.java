import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * 信息板
 */
public class Infomationboard extends Rectangle {
    public Infomationboard(Pane pane,int layoutX,int layoutY,double width,double height) {
        this.setX(layoutX);
        this.setY(layoutY);
        this.setWidth(width);
        this.setHeight(height);
        this.setArcHeight(20);
        this.setArcWidth(20);
        this.setFill(new javafx.scene.paint.Color(0.99, 0.97, 0.90, 1));

        DropShadow dscb = new DropShadow();
        dscb.setOffsetY(7.0);
        dscb.setOffsetX(7.0);
        dscb.setRadius(20);
        dscb.setColor(new javafx.scene.paint.Color(0, 0, 0, 0.5));
        this.setEffect(dscb);
        pane.getChildren().add(this);
    }
}
