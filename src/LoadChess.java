import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class LoadChess extends Stage {
    private static final int SCEHEIGHT = 750;//Scene的高度
    private static final int SCEWIDTH = 700;//Scene的宽度
    private static Checkerboard checkerboard;
    private static ArrayList<Chess> chessArrayList = new ArrayList<>();
    private final Pane pane;
    private Stage stage;
    private Button readBtn;
    private Button prevBtn;
    private Button nextBtn;
    private Button exitBtn;
    private Slider slider;
    private final Label[] lables = new Label[3];
    private int cheIndex;//从0开始


    public LoadChess() {
        this.stage = this;
        pane = new Pane();
        Group group = new Group();
        group.getChildren().addAll(ResManager.getImageView(),pane);
        checkerboard = new Checkerboard(pane, 50, 50);
        Scene scene = new Scene(group, SCEWIDTH, SCEHEIGHT);
        stage.setScene(scene);//primaryStage表示图形界面

        getBtns();
        read();
        prev();
        next();
        stageClose();
        exit();
        getLable();
    }

    private void getLable() {
        lables[0] = new Label();
        lables[0].setLayoutX(40);
        lables[0].setLayoutY(10);
        lables[1] = new Label();
        lables[1].setLayoutX(240);
        lables[1].setLayoutY(10);
        lables[2] = new Label();
        lables[2].setLayoutX(440);
        lables[2].setLayoutY(10);
        pane.getChildren().addAll(lables);
    }

    public void stageClose() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setHeaderText("确定退出游戏吗？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    stage.close();
                } else {
                    event.consume();
                }
            }
        });
    }

    public void getBtns() {

        int baseX = 50;
        int baseY = 700;

        prevBtn = new Button("<");
        prevBtn.setLayoutX(baseX);
        prevBtn.setLayoutY(baseY);
        nextBtn = new Button(">");
        nextBtn.setLayoutX(baseX * 2);
        nextBtn.setLayoutY(baseY);
        readBtn = new Button("载入棋谱");
        readBtn.setLayoutX(baseX * 9);
        readBtn.setLayoutY(baseY);
        exitBtn = new Button("返回主菜单");
        exitBtn.setLayoutX(baseX * 11);
        exitBtn.setLayoutY(baseY);
        slider = new Slider();
        slider.setLayoutX(50 * 3);
        slider.setLayoutY(700);
        slider.setPrefWidth(280);

        pane.getChildren().addAll(readBtn, prevBtn, nextBtn, exitBtn,slider);
    }

    public void read() {
        prevBtn.setDisable(true);
        nextBtn.setDisable(true);
        readBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cheIndex != 0) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("重新载入");
                    alert.setHeaderText("确定重新载入棋谱吗？当前棋盘将会清空。");
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional.get() == ButtonType.OK) {
                        doRead();
                    } else {
                        event.consume();
                    }
                } else {
                    doRead();
                }
            }
        });
    }

    public void doRead() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Player_Npc.class.getClassLoader().getResource("").getPath() + "save"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            prevBtn.setDisable(true);
            nextBtn.setDisable(true);
            pane.getChildren().removeIf(new Predicate<Node>() {
                @Override
                public boolean test(Node node) {
                    return node instanceof ChessCircle;
                }
            });
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(
                        new FileInputStream(file)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object o = objectInputStream.readObject();
                if (o instanceof ArrayList) {
                    chessArrayList = (ArrayList<Chess>) o;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < chessArrayList.size(); i++) {
                ChessCircle chessCircle = new ChessCircle(checkerboard, chessArrayList.get(i).getChessX(), chessArrayList.get(i).getChessY(), chessArrayList.get(i).isBlack(), chessArrayList.get(i).getCount(), chessArrayList.get(i).getStepTime());
                addCheHover(chessCircle);
            }
            cheIndex = chessArrayList.size() - 1;

            slider.setMax(chessArrayList.size());
            slider.setMin(1);
            slider.setValue(chessArrayList.size());
            slider.setSnapToTicks(true);
            slider.setMajorTickUnit(1);
            slider.setMinorTickCount(0);
//        slider.setBlockIncrement(3);
//            slider.setShowTickLabels(true);
//            slider.setShowTickMarks(true);
            getSliderValue();
            updatetarget();
            prevBtn.setDisable(false);
        }
    }

    public void prev() {

        prevBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nextBtn.setDisable(false);
                pane.getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        if (node instanceof ChessCircle && ((ChessCircle) node).getCount() == cheIndex) {
                            cheIndex--;
                            return true;
                        }
                        return false;
                    }
                });
                slider.setValue(cheIndex + 1);
                updatetarget();
                if (cheIndex == 0) {
                    prevBtn.setDisable(true);
                }
            }
        });
    }

    public void next() {
        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                prevBtn.setDisable(false);
                ChessCircle chessCircle = new ChessCircle(checkerboard, chessArrayList.get(++cheIndex).getChessX(), chessArrayList.get(cheIndex).getChessY(), chessArrayList.get(cheIndex).isBlack(), chessArrayList.get(cheIndex).getCount(),chessArrayList.get(cheIndex).getStepTime());
                addCheHover(chessCircle);
                slider.setValue(cheIndex + 1);
                if (cheIndex == chessArrayList.size() - 1) {
                    nextBtn.setDisable(true);
                }
                updatetarget();
            }
        });
    }

    public void getSliderValue() {
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int slideIndex = newValue.intValue() - 1;//从0开始
                if (cheIndex > slideIndex) {
                    pane.getChildren().removeIf(new Predicate<Node>() {
                        @Override
                        public boolean test(Node node) {
                            return node instanceof ChessCircle && ((ChessCircle) node).getCount() > slideIndex;
                        }
                    });
                    cheIndex = slideIndex;
                }

                if (cheIndex < slideIndex) {
                    for (int i = cheIndex + 1; i <= slideIndex; i++) {
                        ChessCircle chessCircle = new ChessCircle(checkerboard, chessArrayList.get(i).getChessX(), chessArrayList.get(i).getChessY(), chessArrayList.get(i).isBlack(), chessArrayList.get(i).getCount(),chessArrayList.get(i).getStepTime());
                        addCheHover(chessCircle);
                    }
                    cheIndex = slideIndex;
                }

                if (cheIndex == 0) {
                    prevBtn.setDisable(true);
                    nextBtn.setDisable(false);
                } else if (cheIndex == chessArrayList.size() - 1) {
                    nextBtn.setDisable(true);
                    prevBtn.setDisable(false);
                } else {
                    prevBtn.setDisable(false);
                    nextBtn.setDisable(false);
                }


                updatetarget();
            }
        });

    }

    private void exit() {
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setHeaderText("确定返回主菜单吗？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    stage.close();
                    MainMenu.getStage().show();
                } else {
                    event.consume();
                }
            }
        });
    }

    public void addCheHover(ChessCircle chessCircle) {
        chessCircle.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        String[] info = chessCircle.getInfo();
                        lables[0].setText(info[0]);
                        lables[1].setText(info[1]);
                        lables[2].setText(info[2]);
//                        infoCircle.setBlack(Integer.parseInt(info[2].split("：")[1]) % 2 == 1);
                    }
                });
    }

    private void updatetarget() {
        pane.getChildren().removeIf(new Predicate<Node>() {
            @Override
            public boolean test(Node node) {
                return node instanceof TargetIco;
            }
        });
        new TargetIco(checkerboard, cheIndex % 2 == 0, chessArrayList.get(cheIndex).getChessX(), chessArrayList.get(cheIndex).getChessY());
    }
}
