import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import message.*;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Properties;

public class MainMenu extends Application {
    private static Stage stage;
    private static Pane pane;
    private static Button player_npcBtn;
    private static Button npc_npcBtn;
    private static Button player_playerBtn;
    private static Button loadBtn;
    private static Button exitBtn;
    private static Button setBtn;
    private static TextField midText;
    private static TextField mipText;
    private static TextField mportText;
    private static TextField oipText;
    private static TextField oportText;
    private static String OID;

    @Override
    public void start(Stage primaryStage) throws Exception {
        getPane();
        Group group = new Group();
        group.getChildren().addAll(ResManager.getImageView(),pane);
        Scene scene = new Scene(group, 600, 400);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
        ResManager.playBGM();
        initialBGM_Sound();
        getPro();
        setPro();

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setHeaderText("确定游戏退出吗？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    stage.close();
                } else {
                    event.consume();
                }
            }
        });

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

        player_npcBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                Player_Npc player_npc = new Player_Npc();
                player_npc.show();
            }
        });

        npc_npcBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                Npc_Npc npc_npc = new Npc_Npc();
                npc_npc.show();
            }
        });

        player_playerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                Player_Player player_player = new Player_Player();
                player_player.initOwner(stage);
                player_player.show();

                //region 接收端
                new Thread() {
                    public void run() {
                        try {
                            //创建Socket对象
                            ServerSocket ss = new ServerSocket(Integer.parseInt(mportText.getText()));
                            while (true) {
                                //监听连接
                                Socket s = ss.accept();
                                //获取管道输入流对象
                                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                                //读取数据
                                Object o = ois.readObject();

                                if (!(o instanceof Message)) {
                                    return;
                                }
                                if (o instanceof ConnectionMessage) {//第一次连接消息，接收到主场发送过来的请求
                                    ConnectionMessage connectionMessage = (ConnectionMessage) o;
                                    //region 删除对方正在连接弹窗
                                    player_player.alertClose(Player_Player.getConnectionAlert());
                                    //endregion
                                    //region 记录对方ID
                                    OID = connectionMessage.getOID();
                                    //endregion

                                    //region 显示对方信息

                                    if (connectionMessage.getRandomNum() == 0) {
                                        player_player.alertInfo("已连接 " + OID + "！我方为黑棋，获得先行资格！");
                                        player_player.setFalgInt(0);
                                    } else if (connectionMessage.getRandomNum() == 1) {
                                        player_player.alertInfo("已连接 " + OID + "！我方为白棋，对方获得先行资格！");
                                        player_player.setFalgInt(1);
                                    }
                                    //endregion
                                    player_player.sendMessage(new ReConnectionMessage(midText.getText(), connectionMessage.getRandomNum()));
                                } else if (o instanceof ReConnectionMessage) {//第二次连接消息，接收到主场发送过来的请求
                                    ReConnectionMessage reconnectionMessage = (ReConnectionMessage) o;
                                    OID = reconnectionMessage.getOID();
                                    if (reconnectionMessage.getRandomNum() == 0) {
                                        player_player.alertInfo("已连接 " + OID + "！我方为白棋，对方获得先行资格！");
                                        player_player.setFalgInt(1);
                                    } else if(reconnectionMessage.getRandomNum() == 1){
                                        player_player.alertInfo("已连接 " + OID + "！我方为黑棋，获得先行资格！");
                                        player_player.setFalgInt(0);
                                    }
                                } else if (o instanceof ChessMessage) {//落子
                                    ChessMessage chessMessage = (ChessMessage) o;
                                    player_player.updateChess(chessMessage);
                                } else if (o instanceof RegretMessage) {//悔棋
                                    RegretMessage regretMessage = (RegretMessage) o;
                                    if ("request".equals(regretMessage.getInfo())) {
                                        player_player.answerRegreChess();
                                    } else if ("answer".equals(regretMessage.getInfo())) {
                                        player_player.alertClose(player_player.getRegretAlert());
                                        if (regretMessage.isAllowregret()) {
                                            player_player.doRegrechess(regretMessage.getNum());
                                        } else {
                                            player_player.alertInfo("对方拒绝悔棋");
                                        }
                                        player_player.alertClose(player_player.getRegretAlert());
                                    }

                                } else if (o instanceof RestarMesage) {//重开
                                    RestarMesage restarMesage = (RestarMesage) o;
                                    if ("request".equals(restarMesage.getInfo())) {
                                        player_player.answerRestar();
                                    } else if ("answer".equals(restarMesage.getInfo())) {
                                        player_player.alertClose(player_player.getRestarAlert());
                                        if (restarMesage.isAllowRestat()) {
                                            player_player.doRestar();
                                        } else {
                                            player_player.alertInfo("对方拒绝重新开局");
                                        }
                                    }
                                } else if (o instanceof ChatMessage) {//聊天
                                    String info = ((ChatMessage) o).getInfo();
                                    player_player.updateMessageArea(info, OID);
                                } else if(o instanceof EscapeMessage) {
                                    player_player.alertInfo("对方已逃跑，您取得胜利！");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        setBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                Setting_Of_Game settingOfGame = Setting_Of_Game.getInstance();
                settingOfGame.show();

            }
        });

        loadBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                LoadChess loadChess = new LoadChess();
                loadChess.show();
            }
        });


    }

    public void initialBGM_Sound() {
        Properties properties = ResManager.getProperties();
        boolean bgmOpened = Boolean.parseBoolean(properties.getProperty("BGMOpened"));
        boolean soundOpened = Boolean.parseBoolean(properties.getProperty("SoundOpened"));
        ResManager.getBgm().play();
        ResManager.getBgm().setMute(!bgmOpened);
        ResManager.getFallSound().setMute(!soundOpened);
        ResManager.getWinSound().setMute(!soundOpened);
    }

    public void getPro() {

        midText.insertText(0, ResManager.getProperties().getProperty("MID"));

        mipText.insertText(0, ResManager.getProperties().getProperty("MIP"));

        mportText.insertText(0, ResManager.getProperties().getProperty("MPort"));

        oipText.insertText(0, ResManager.getProperties().getProperty("OIP"));

        oportText.insertText(0, ResManager.getProperties().getProperty("OPort"));

    }

    public void setPro() {
        midText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ResManager.setProperties("MID", midText.getText().trim());
            }
        });
        mipText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ResManager.setProperties("MIP", mipText.getText().trim());
            }
        });
        mportText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ResManager.setProperties("MPort", mportText.getText().trim());

            }
        });
        oipText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ResManager.setProperties("OIP", oipText.getText().trim());

            }
        });
        oportText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ResManager.setProperties("OPort", oportText.getText().trim());

            }
        });
    }

    public void getPane() {
        pane = new Pane();
        int prefWidthBtn = 180;
        int prefHeightBtn = 50;
        int layoutXBtn = 30;
        player_npcBtn = new Button("玩家VS人机");
        npc_npcBtn = new Button("人机VS人机");
        player_playerBtn = new Button("玩家VS玩家");
        loadBtn = new Button("加载棋谱");
        setBtn = new Button("游戏设置");
        exitBtn = new Button("退出游戏");

        player_npcBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        player_npcBtn.setLayoutX(layoutXBtn);
        player_npcBtn.setLayoutY(30);
        npc_npcBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        npc_npcBtn.setLayoutX(layoutXBtn);
        npc_npcBtn.setLayoutY(100);
        player_playerBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        player_playerBtn.setLayoutX(layoutXBtn);
        player_playerBtn.setLayoutY(170);
        loadBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        loadBtn.setLayoutX(layoutXBtn);
        loadBtn.setLayoutY(240);
        setBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        setBtn.setLayoutX(layoutXBtn);
        setBtn.setLayoutY(310);
        exitBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        exitBtn.setLayoutX(layoutXBtn + 310);
        exitBtn.setLayoutY(310);

        pane.getChildren().addAll(player_npcBtn, npc_npcBtn, player_playerBtn, loadBtn, setBtn, exitBtn);


        Label midLab = new Label("我的ID：");
        midLab.setLayoutX(250);
        midLab.setLayoutY(50);
        Label mipLab = new Label("我的IP：");
        mipLab.setLayoutX(250);
        mipLab.setLayoutY(100);
        Label mportLab = new Label("我的端口：");
        mportLab.setLayoutX(250);
        mportLab.setLayoutY(150);
        Label oipLab = new Label("对方IP：");
        oipLab.setLayoutX(250);
        oipLab.setLayoutY(200);
        Label oportLab = new Label("对方端口：");
        oportLab.setLayoutX(250);
        oportLab.setLayoutY(250);

        pane.getChildren().addAll(midLab, mipLab, mportLab, oipLab, oportLab);


        midText = new TextField();
        midText.setLayoutX(320);
        midText.setLayoutY(50);
        mipText = new TextField();
        mipText.setLayoutX(320);
        mipText.setLayoutY(100);
        mportText = new TextField();
        mportText.setLayoutX(320);
        mportText.setLayoutY(150);
        oipText = new TextField();
        oipText.setLayoutX(320);
        oipText.setLayoutY(200);
        oportText = new TextField();
        oportText.setLayoutX(320);
        oportText.setLayoutY(250);

        pane.getChildren().addAll(midText, mipText, mportText, oipText, oportText);

    }


    public static TextField getOipText() {
        return oipText;
    }


    public static TextField getOportText() {
        return oportText;
    }

    public static TextField getMidText() {
        return midText;
    }

    public static Stage getStage() {
        return stage;
    }


    public static String getOID() {
        return OID;
    }


}
