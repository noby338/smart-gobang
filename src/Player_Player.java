import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import message.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class Player_Player extends Stage {
    private static final int SCEHEIGHT = 800;//Scene的高度
    private static final int SCEWIDTH = 1000;//Scene的宽度
    private static Checkerboard checkerboard;
    private static ArrayList<Chess> chessArrayList = new ArrayList<>();
    private int[][] coordinateArr = new int[Checkerboard.getCHELINENUM()][Checkerboard.getCHELINENUM()];//棋的坐标记录
    private Pane pane;
    private boolean isGameOver = false;
    private static boolean showWeightIsOpen = false;
    private int falgInt;
    private static int removeNum;
    private Button regretBtn;
    private Button restartBtn;
    private Button writeBtn;
    private Button exitBtn;
    private Button sendChatBtn;
    private Alert regretAlert;
    private Alert restarAlert;
    private TextArea myTextArea;
    private TextArea messageTextArea;
    private static Alert connectionAlert;
    private final Stage stage;

    public Player_Player() {
        this.stage = this;
        initialPane();
        updatePane();
        Group group = new Group();
        group.getChildren().addAll(ResManager.getImageView(),pane);
        Scene scene = new Scene(group, SCEWIDTH, SCEHEIGHT);
        stage.setScene(scene);//primaryStage表示图形界面
    }

    public void updatePane() {
        connect();
        sendChat();
        requestResStart();
        write(stage);
        exit();
        requestRegreChess();
        playChess();
        stageClose();
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

    public void connect() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectionAlert = new Alert(Alert.AlertType.CONFIRMATION);
                connectionAlert.setHeaderText("待双方都进入该页面后，任意一方点击确定即可进行游戏！");
                connectionAlert.setContentText("棋色将随机分配！该局规则无禁手！");
                connectionAlert.initOwner(stage);
                Optional<ButtonType> optional = connectionAlert.showAndWait();
                if (optional.get() == ButtonType.OK) {//点击确定连接的一方为主场
                    ConnectionMessage connectionMessage = new ConnectionMessage(MainMenu.getMidText().getText(),(int)(Math.random()*2));
                    sendMessage(connectionMessage);//主场发送连接请求
                }
            }
        });
    }

    public void sendChat() {
        sendChatBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = myTextArea.getText();
                if ("".equals(text)) {
                    return;
                }
                String[] disabledWords = ResManager.getProperties().getProperty("DisabledWord").split(";");
                for (String disabledWord : disabledWords) {
                    if (text.contains(disabledWord)) {
                        String re = "";
                        for (int i = 0; i < disabledWord.length(); i++) {
                            re = re.concat("*");
                        }
                        text = text.replace(disabledWord, re);
                    }
                }
                sendMessage(new ChatMessage(text));
                updateMessageArea(text,MainMenu.getMidText().getText());
                myTextArea.setText("");
            }
        });
    }

    public void initialPane() {
        pane = new Pane();
        //编辑画板
        checkerboard = new Checkerboard(pane, 50, 50);
        new Infomationboard(pane,700,50,250,600);
        getButtons();
        getTextArea();
        getChoiceBox();
    }

    public void getChoiceBox() {

        String conversation = ResManager.getProperties().getProperty("Conversation");
        String[] infos = conversation.split(";");
        ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(infos));
        choiceBox.setPrefWidth(230);
        choiceBox.setLayoutX(710);
        choiceBox.setLayoutY(487);
        choiceBox.setCenterShape(false);

        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                sendMessage(new ChatMessage(infos[new_value.intValue()]));
                updateMessageArea(infos[new_value.intValue()], MainMenu.getMidText().getText());
            }
        });


        pane.getChildren().add(choiceBox);


    }

    public void getTextArea() {
        messageTextArea = new TextArea();
        messageTextArea.setLayoutX(710);
        messageTextArea.setLayoutY(270);
        messageTextArea.setMaxWidth(230);
        messageTextArea.setMaxHeight(200);
        messageTextArea.setSnapToPixel(true);
        messageTextArea.setEditable(false);
        messageTextArea.setText("");

        myTextArea = new TextArea();
        myTextArea.setLayoutX(710);
        myTextArea.setLayoutY(520);
        myTextArea.setMaxWidth(230);
        myTextArea.setMaxHeight(80);
        myTextArea.setSnapToPixel(true);
        myTextArea.setEditable(true);

        pane.getChildren().addAll(myTextArea, messageTextArea);
    }

    public void getButtons() {
        int baseX = 50;
        int baseY = 700;

        restartBtn = new Button("请求重开");
        restartBtn.setLayoutX(baseX);
        restartBtn.setLayoutY(baseY);

        regretBtn = new Button("请求悔棋");
        regretBtn.setLayoutX(baseX * 3);
        regretBtn.setLayoutY(baseY);

        writeBtn = new Button("保存棋谱");
        writeBtn.setLayoutX(baseX * 9);
        writeBtn.setLayoutY(baseY);

        exitBtn = new Button("返回主菜单");
        exitBtn.setLayoutX(baseX * 11);
        exitBtn.setLayoutY(baseY);

        sendChatBtn = new Button("发送消息");
        sendChatBtn.setLayoutX(855);
        sendChatBtn.setLayoutY(610);

        pane.getChildren().addAll(restartBtn, regretBtn, writeBtn, exitBtn,sendChatBtn);
    }

    public void updateChess(ChessMessage chessMessage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int chessX = chessMessage.getChessX();
                int chessY = chessMessage.getChessY();
                coordinateArr[chessX][chessY] = chessArrayList.size() % 2 == 0 ? 1 : -1;
                new Chess(checkerboard, chessArrayList, chessX, chessY);
                ResManager.playFallSound();
                updateTargetIco(chessArrayList.size() - 1);
                isWin(chessX, chessY);
                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        System.out.printf("%-4d", coordinateArr[j][i]);
                    }
                    System.out.println();
                }
                System.out.println(Chess.getChessArrayList());
            }
        });

    }

    public void playChess() {

        //region 获取鼠标点击事件
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if ((chessArrayList.size() + falgInt) % 2 == 1 || isGameOver) {
                    return;
                }
                pane.getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        return node instanceof ChessRing;
                    }
                });

                double clickX = event.getX();//获取点击到的坐标
                double clickY = event.getY();
                //region 对点击超出棋盘的点进行过滤
                if (!(checkerboard.getCheX() < clickX && clickX < checkerboard.getCheX() + Checkerboard.getCHESIDELENGTH() && checkerboard.getCheY() < clickY && clickY < checkerboard.getCheY() + Checkerboard.getCHESIDELENGTH())) {
                    return;
                }
                //endregion

                int chessX = (int) (clickX - Checkerboard.getCHEMARGIN() - checkerboard.getCheX() + Checkerboard.getCHEPADDING() / 2) / Checkerboard.getCHEPADDING();//计算棋盘棋子的坐标
                int chessY = (int) (clickY - Checkerboard.getCHEMARGIN() - checkerboard.getCheY() + Checkerboard.getCHEPADDING() / 2) / Checkerboard.getCHEPADDING();

                //region 对已经落子的点进行过滤
                if (coordinateArr[chessX][chessY] == 1 || coordinateArr[chessX][chessY] == -1) {
                    return;
                }
                //endregion

                coordinateArr[chessX][chessY] = chessArrayList.size() % 2 == 0 ? 1 : -1;
                new Chess(checkerboard, chessArrayList, chessX, chessY);
                ResManager.playFallSound();

                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        System.out.printf("%-4d", coordinateArr[j][i]);
                    }
                    System.out.println();
                }
                updateTargetIco(chessArrayList.size() - 1);
                isWin(chessX, chessY);
                System.out.println(Chess.getChessArrayList());

                //region 发送端
                Socket socket = null;
                try {
                    socket = new Socket(MainMenu.getOipText().getText(), Integer.parseInt(MainMenu.getOportText().getText()));
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ChessMessage chessMessage = new ChessMessage(chessX, chessY);
                    oos.writeObject(chessMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //endregion

            }
        });

        //endregion
    }

    public void isWin(int chessX, int chessY) {
        int num = coordinateArr[chessX][chessY];
        int xr = 0;//x方向向右
        int xl = 0;//x方向向左
        int yr = 0;
        int yl = 0;
        int xyr = 0;
        int xyl = 0;
        int yxr = 0;
        int yxl = 0;

        //region x方向
        while (chessX + xr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + xr + 1][chessY] == num) {
            xr++;
        }
        while (chessX - xl - 1 >= 0 && coordinateArr[chessX - xl - 1][chessY] == num) {
            xl++;
        }
        //endregion

        //region y方向
        while (chessY + yr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX][chessY + yr + 1] == num) {
            yr++;
        }
        while (chessY - yl - 1 >= 0 && coordinateArr[chessX][chessY - yl - 1] == num) {
            yl++;
        }
        //endregion

        //region 正斜杠方向
        while (chessX + xyr + 1 < Checkerboard.getCHELINENUM() && chessY - xyr - 1 >= 0 && coordinateArr[chessX + xyr + 1][chessY - xyr - 1] == num) {
            xyr++;
        }
        while (chessX - xyl - 1 >= 0 && chessY + xyl + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX - xyl - 1][chessY + xyl + 1] == num) {
            xyl++;
        }
        //endregion

        //region 反斜杠方向
        while (chessY + yxr + 1 < Checkerboard.getCHELINENUM() && chessX + yxr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + yxr + 1][chessY + yxr + 1] == num) {
            yxr++;
        }
        while (chessY - yxl - 1 >= 0 && chessX - yxl - 1 >= 0 && coordinateArr[chessX - yxl - 1][chessY - yxl - 1] == num) {
            yxl++;
        }
        //endregion


        if (xl + xr >= 4) {
            for (int i = -xl; i <= xr; i++) {
                new ChessRing(checkerboard, chessX + i, chessY);
            }
        }

        if (yl + yr >= 4) {
            for (int i = -yl; i <= yr; i++) {
                new ChessRing(checkerboard, chessX, chessY + i);
            }
        }
        if (xyl + xyr >= 4) {
            for (int i = -xyl; i <= xyr; i++) {
                new ChessRing(checkerboard, chessX + i, chessY - i);
            }
        }
        if (yxl + yxr >= 4) {
            for (int i = -yxl; i <= yxr; i++) {
                new ChessRing(checkerboard, chessX + i, chessY + i);
            }
        }


        if (xl + xr >= 4 || yl + yr >= 4 || yxl + yxr >= 4 || xyl + xyr >= 4) {
            ResManager.playWinSound();
            isGameOver(true);
        }

    }

    public void updateTargetIco(int count) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        return node instanceof TargetIco;
                    }
                });
                if (count < 0) {
                    return;
                }
                int chessX = chessArrayList.get(count).getChessX();
                int chessY = chessArrayList.get(count).getChessY();
                new TargetIco(checkerboard, chessArrayList.size() % 2 == 1, chessX, chessY);
            }
        });

    }

    public void isGameOver(boolean isGameOver) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    Player_Player.this.isGameOver = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("游戏结束");
                    alert.initOwner(stage);
                    if (chessArrayList.size() % 2 == 1) {
                        alert.setHeaderText("黑棋胜利");
                    } else {
                        alert.setHeaderText("白棋胜利");
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            alert.show();
                        }
                    });
                } else {
                    Player_Player.this.isGameOver = false;
                    pane.getChildren().removeIf(new Predicate<Node>() {
                        @Override
                        public boolean test(Node node) {
                            return node instanceof ChessRing;
                        }
                    });
                }
            }
        });
    }

    public void requestResStart() {
//        restartBtn.setDisable(true);

        restartBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //region 发送重开请求
                sendMessage(new RestarMesage("request"));
                //endregion

                //region 弹窗等待
                restarAlert = new Alert(Alert.AlertType.CONFIRMATION);
                restarAlert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
                restarAlert.setTitle("重新开局");
                restarAlert.setHeaderText("等待对方回应");
                restarAlert.initOwner(stage);
                restarAlert.show();
                //endregion
            }
        });
    }

    public void answerRestar() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("重新开局");
                alert.setHeaderText("对方请求重新开局");
                alert.setContentText("同意重新开局吗？");
                alert.initOwner(stage);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    //region 同意重新开局消息发送
                    sendMessage(new RestarMesage("answer", true));
                    //endregion
                    doRestar();
                } else {
                    sendMessage(new RestarMesage("answer", false));
                }
            }
        });
    }

    public void doRestar() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        if (node instanceof ChessCircle && ((ChessCircle) node).getCount() == -1) {
                            return false;
                        }
                        return node instanceof ChessCircle || node instanceof TargetIco || node instanceof Text;
                    }
                });
            }
        });
        chessArrayList.clear();
        coordinateArr = new int[Checkerboard.getCHELINENUM()][Checkerboard.getCHELINENUM()];
        isGameOver(false);
    }

    public void requestRegreChess() {
//        regretBtn.setDisable(true);

        regretBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMessage(new RegretMessage("request"));
                //region 弹窗等待
                regretAlert = new Alert(Alert.AlertType.CONFIRMATION);
                regretAlert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
                regretAlert.setTitle("悔棋");
                regretAlert.setHeaderText("等待对方回应");
                regretAlert.initOwner(stage);
                regretAlert.show();
                //endregion
            }
        });

    }

    public void answerRegreChess() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("悔棋");
                alert.setHeaderText("对方请求悔棋");
                alert.setContentText("同意悔棋吗？");
                alert.initOwner(stage);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    //region 同意悔棋消息发送
                    doRegrechess();
                    sendMessage(new RegretMessage("answer", true, removeNum));
                    //endregion
                } else {
                    sendMessage(new RegretMessage("answer", false));
                }
            }
        });

    }

    public void doRegrechess() {
        isGameOver(false);
        chessArrayList.get(chessArrayList.size() - 1).removeChessCircle();
        coordinateArr[chessArrayList.get(chessArrayList.size() - 1).getChessX()][chessArrayList.get(chessArrayList.size() - 1).getChessY()] = 0;
        chessArrayList.remove(chessArrayList.size() - 1);
        Player_Player.setRemoveNum(1);

        if ((chessArrayList.size() + falgInt) % 2 == 0) {//判断是否删除第二颗，是自己的不删，是对方的删
            chessArrayList.get(chessArrayList.size() - 1).removeChessCircle();
            coordinateArr[chessArrayList.get(chessArrayList.size() - 1).getChessX()][chessArrayList.get(chessArrayList.size() - 1).getChessY()] = 0;
            chessArrayList.remove(chessArrayList.size() - 1);
            Player_Player.setRemoveNum(2);
        }
        updateTargetIco(chessArrayList.size() - 1);
        System.out.println(removeNum);
    }

    public void doRegrechess(int num) {

        isGameOver(false);
        chessArrayList.get(chessArrayList.size() - 1).removeChessCircle();
        coordinateArr[chessArrayList.get(chessArrayList.size() - 1).getChessX()][chessArrayList.get(chessArrayList.size() - 1).getChessY()] = 0;
        chessArrayList.remove(chessArrayList.size() - 1);


        if (num == 2) {
            chessArrayList.get(chessArrayList.size() - 1).removeChessCircle();
            coordinateArr[chessArrayList.get(chessArrayList.size() - 1).getChessX()][chessArrayList.get(chessArrayList.size() - 1).getChessY()] = 0;
            chessArrayList.remove(chessArrayList.size() - 1);
        }
        updateTargetIco(chessArrayList.size() - 1);
    }

    public void updateMessageArea(String info, String id) {
        Calendar calendar = Calendar.getInstance();
        String format = String.format(id + ":     %02d:%02d:%02d\n", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        messageTextArea.insertText(0, info + "\n");
        messageTextArea.insertText(0, format);
    }

    public void sendMessage(Message message) {
        //region 发送悔棋信息
        Socket socket = null;
        try {
            socket = new Socket(MainMenu.getOipText().getText(), Integer.parseInt(MainMenu.getOportText().getText()));
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //endregion
    }

    public void exit() {
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setHeaderText("确定返回主菜单吗？");
                alert.setContentText("退出将会被视为逃跑，对方将取得胜利。");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {

                    sendMessage(new EscapeMessage());
                    stage.close();
                    MainMenu.getStage().show();
                } else {
                    event.consume();
                }
            }
        });
    }

    public void alertClose(Alert alert) {
        for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
            if (bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                Button cancelButton = (Button) alert.getDialogPane().lookupButton(bt);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        cancelButton.fire();
                    }
                });
                break;
            }
        }
    }

    public void alertInfo(String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(string);
                alert.initOwner(stage);
                alert.showAndWait();
            }
        });

    }

    public void write(Stage primaryStage) {
        writeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(MainMenu.getMidText().getText() + "-" +  MainMenu.getOID() + new SimpleDateFormat("_MM-dd_HH-mm-ss").format(new Date()) + ".txt");
                fileChooser.setInitialDirectory(new File(Player_Npc.class.getClassLoader().getResource("").getPath() + "save"));
                ObjectOutputStream objectOutputStream = null;
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file == null) {
                    return;
                }
                try {
                    objectOutputStream = new ObjectOutputStream(
                            new FileOutputStream(file)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    objectOutputStream.writeObject(chessArrayList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Alert getConnectionAlert() {
        return connectionAlert;
    }

    public void setFalgInt(int falgInt) {
        this.falgInt = falgInt;
    }

    public Alert getRegretAlert() {
        return regretAlert;
    }

    public Alert getRestarAlert() {
        return restarAlert;
    }

    public static void setRemoveNum(int removeNum) {
        Player_Player.removeNum = removeNum;
    }
}
