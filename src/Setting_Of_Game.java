import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Setting_Of_Game extends Stage {
    private static Stage stage;
    private static Pane pane;
    private static Button bgmBtn;
    private static Button soundBtn;
    private static Button openProBtn;
    private static Button exitBtn;

    private static Setting_Of_Game instance = new Setting_Of_Game();

    private Setting_Of_Game() {
        this.stage = this;
        getPane();
        Scene scene = new Scene(pane, 600, 400, Color.SKYBLUE);
        stage.setScene(scene);//primaryStage表示图形界面

        bgmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ResManager.getBgm().isMute()) {
                    bgmBtn.setText("音乐已开启");
                    ResManager.getBgm().setMute(false);
                }else {
                    bgmBtn.setText("音乐已关闭");
                    ResManager.getBgm().setMute(true);
                }
            }
        });


        soundBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ResManager.getFallSound().isMute() && ResManager.getWinSound().isMute()) {
                    soundBtn.setText("音效已开启");
                    ResManager.getFallSound().setMute(false);
                    ResManager.getWinSound().setMute(false);
                }else {
                    soundBtn.setText("音效已关闭");
                    ResManager.getFallSound().setMute(true);
                    ResManager.getWinSound().setMute(true);
                }
            }
        });

        openProBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().open(new File(ResManager.getProPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                MainMenu.getStage().show();
            }
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();
                MainMenu.getStage().show();
            }
        });
    }

    private void getPane() {
        pane = new Pane();
        int prefWidthBtn = 180;
        int prefHeightBtn = 50;
        int layoutXBtn = 30;
        bgmBtn = new Button();
        if (ResManager.getBgm().isMute()) {
            bgmBtn.setText("音乐已关闭");
        } else{
            bgmBtn.setText("音乐已开启");
        }

        soundBtn = new Button("音效已开启");
        openProBtn = new Button("打开配置文件");
        exitBtn = new Button("返回主菜单");


        bgmBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        bgmBtn.setLayoutX(layoutXBtn);
        bgmBtn.setLayoutY(30);
        soundBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        soundBtn.setLayoutX(layoutXBtn);
        soundBtn.setLayoutY(100);
        openProBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        openProBtn.setLayoutX(layoutXBtn);
        openProBtn.setLayoutY(170);
        exitBtn.setPrefSize(prefWidthBtn, prefHeightBtn);
        exitBtn.setLayoutX(layoutXBtn);
        exitBtn.setLayoutY(240);
        pane.getChildren().addAll(bgmBtn, soundBtn, openProBtn,exitBtn);
    }

    public static Setting_Of_Game getInstance() {
        return instance;
    }
}