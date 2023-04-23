import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

/*
javaFx 为java的图形化工具
 */
public class Npc_Npc extends Stage {//Application 为一个抽象方法，子类必须实现start方法
    private static final int SCEHEIGHT = 800;//Scene的高度
    private static final int SCEWIDTH = 1000;//Scene的宽度
    private static Checkerboard checkerboard;
    private static final ArrayList<Chess> chessArrayList = new ArrayList<>();
    private int[][] coordinateArr = new int[Checkerboard.getCHELINENUM()][Checkerboard.getCHELINENUM()];//棋的坐标记录
    private Pane pane;
    private boolean isGameOver = false;
    private static boolean showWeightIsOpen = false;
    private Timer cheTimer;
    private Timer startTimer;
    private Timer npcTimer;
    private Button regretBtn;
    private Button restartBtn;
    private Button showWeightBtn;
    private Button recommendBtn;
    private Button writeBtn;
    private Button exitBtn;
    private final ProgressBar[] progressBars = new ProgressBar[2];
    private final Label[] lables = new Label[12];
    private Stage stage;
    private long initialMillis;//重新开始的时间点

    public Npc_Npc() {
        this.stage = this;
        getPane();
        Group group = new Group();
        group.getChildren().addAll(ResManager.getImageView(),pane);
        Scene scene = new Scene(group, SCEWIDTH, SCEHEIGHT);
        stage.setScene(scene);//primaryStage表示图形界面

        getInformation();
        getButtons();
        begin();
        setShowWeight();
        removeChess();
        clearChess();
        startTimer();
        exit();
        stageClose();
        write(stage);
        initialMillis = System.currentTimeMillis();
        Chess.setInitialMillis(initialMillis);
    }

    public void getPane() {
        pane = new Pane();
        //编辑画板
        new Infomationboard(pane,700,50,250,600);
        checkerboard = new Checkerboard(pane, 50, 50);
    }

    public void write(Stage primaryStage) {
        writeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("Npc" + "-Npc_" + new SimpleDateFormat("MM-dd_HH-mm-ss").format(new Date()) + ".txt");
                fileChooser.setInitialDirectory(new File(Npc_Npc.class.getClassLoader().getResource("").getPath() + "save"));
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

    public void getButtons() {
        int baseX = 50;
        int baseY = 700;

        restartBtn = new Button("重新开始");
        restartBtn.setLayoutX(baseX);
        restartBtn.setLayoutY(baseY);

        regretBtn = new Button("悔棋");
        regretBtn.setLayoutX(baseX * 3);
        regretBtn.setLayoutY(baseY);

        showWeightBtn = new Button("开启权重显示");
        showWeightBtn.setLayoutX(baseX * 5);
        showWeightBtn.setLayoutY(baseY);

        recommendBtn = new Button("提示");
        recommendBtn.setLayoutX(baseX * 8);
        recommendBtn.setLayoutY(baseY);

        writeBtn = new Button("保存棋谱");
        writeBtn.setLayoutX(baseX * 9);
        writeBtn.setLayoutY(baseY);

        exitBtn = new Button("返回主菜单");
        exitBtn.setLayoutX(baseX * 11);
        exitBtn.setLayoutY(baseY);


        pane.getChildren().addAll(restartBtn, regretBtn, showWeightBtn, recommendBtn, writeBtn, exitBtn);
    }

    public void getInformation() {
        new ChessCircle(checkerboard, 720, 200 + 250, true);
        new ChessCircle(checkerboard, 720, 200, false);
        lables[0] = new Label();
        lables[0].setLayoutX(750);
        lables[0].setLayoutY(100);
        lables[1] = new Label();
        lables[1].setLayoutX(750);
        lables[1].setLayoutY(120);
        lables[2] = new Label();
        lables[2].setLayoutX(750);
        lables[2].setLayoutY(140);
        lables[3] = new Label();
        lables[3].setLayoutX(750);
        lables[3].setLayoutY(160);

        lables[4] = new Label();
        lables[4].setLayoutX(750);
        lables[4].setLayoutY(220);
        lables[4].setText("NPC");
        lables[5] = new Label();
        lables[5].setLayoutX(750);
        lables[5].setLayoutY(240);
        lables[6] = new Label();
        lables[6].setLayoutX(750);
        lables[6].setLayoutY(260);
        lables[7] = new Label();
        lables[7].setLayoutX(750);
        lables[7].setLayoutY(280);

        lables[8] = new Label();
        lables[8].setLayoutX(750);
        lables[8].setLayoutY(470);
        lables[8].setText("NPC");
        lables[9] = new Label();
        lables[9].setLayoutX(750);
        lables[9].setLayoutY(490);
        lables[10] = new Label();
        lables[10].setLayoutX(750);
        lables[10].setLayoutY(510);
        lables[11] = new Label();
        lables[11].setLayoutX(750);
        lables[11].setLayoutY(530);

        progressBars[0] = new ProgressBar(1.0);
        progressBars[0].setLayoutX(750);
        progressBars[0].setLayoutY(190);
        progressBars[0].setPrefWidth(180);

        progressBars[1] = new ProgressBar(1.0);
        progressBars[1].setLayoutX(750);
        progressBars[1].setLayoutY(440);
        progressBars[1].setPrefWidth(180);

        pane.getChildren().addAll(progressBars);
        pane.getChildren().addAll(lables);
    }

    public void updateCheLables(Chess chess) {

        String[] info = chess.getChessCircle().getInfo();
        if (chess.getCount() % 2 == 0) {
            lables[9].setText(info[0]);
            lables[10].setText(info[1]);
            lables[11].setText(info[2]);
        } else {
            lables[5].setText(info[0]);
            lables[6].setText(info[1]);
            lables[7].setText(info[2]);
        }
    }

    public void updateCheLables() {
        Chess.setInitialMillis(System.currentTimeMillis());
        for (int i = 5; i <= 11; i++) {
            if (i == 8) {
                continue;
            }
            lables[i].setText("");
        }
    }

    public void startTimer() {
        if (startTimer != null) {
            startTimer.cancel();
            initialMillis = System.currentTimeMillis();
        }
        startTimer = new Timer();
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int secondTime = (int) (System.currentTimeMillis() - initialMillis) / 1000;
                int second = secondTime % 60;
                int minute = secondTime / 60;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lables[0].setText("局时      ：" + String.format("%02d", minute) + ":" + String.format("%02d", second));
                    }
                });
            }
        }, 1000, 1000);
    }

    public void ContinueStartTimer() {
        startTimer = new Timer();
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int secondTime = (int) (System.currentTimeMillis() - initialMillis) / 1000;
                int second = secondTime % 60;
                int minute = secondTime / 60;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lables[0].setText("局时      ：" + String.format("%02d", minute) + ":" + String.format("%02d", second));
                    }
                });
            }
        }, 1000, 1000);
    }

    public void cheCountdown() {
        if (cheTimer != null) {
            progressBars[0].setProgress(1.0);
            progressBars[1].setProgress(1.0);
            cheTimer.cancel();
        }
        cheTimer = new Timer();
        cheTimer.schedule(new TimerTask() {
            final double countDownTime = 10;//单位为秒
            double time = 0;

            @Override
            public void run() {
                time += 0.01;
                if ((countDownTime - time) / countDownTime <= 0) {
                    cheTimer.cancel();
                    isGameOver(true);
                } else if (chessArrayList.size() % 2 == 1) {
                    progressBars[0].setProgress((countDownTime - time) / countDownTime);
                } else {
                    progressBars[1].setProgress((countDownTime - time) / countDownTime);
                }
            }
        }, 0, 10);
    }

    public void showWeight() {
        pane.getChildren().removeIf(new Predicate<Node>() {
            @Override
            public boolean test(Node node) {
                return node instanceof Text;
            }
        });
        if (!showWeightIsOpen) {
            return;
        }

        for (int i = 0; i < coordinateArr.length; i++) {
            for (int j = 0; j < coordinateArr.length; j++) {
                if (coordinateArr[i][j] != 0 && coordinateArr[i][j] != -1 && coordinateArr[i][j] != 1) {
                    Text text = new Text(i * Checkerboard.getCHEPADDING() + Checkerboard.getCHEPADDING() + checkerboard.getCheX() - 20, j * Checkerboard.getCHEPADDING() + Checkerboard.getCHEPADDING() + checkerboard.getCheX() - 10, coordinateArr[i][j] + "");
                    text.setFill(Color.rgb(0, 0, 255, 1));
                    pane.getChildren().add(text);
                }
            }
        }
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
    public void exit() {
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出");
                alert.setHeaderText("确定返回主菜单吗？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    cheTimer.cancel();
                    startTimer.cancel();
                    npcTimer.cancel();
                    stage.close();
                    MainMenu.getStage().show();
                } else {
                    event.consume();
                }
            }
        });
    }

    public void addTargetIco(int count) {
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

    public void begin() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                npcTimer = new Timer();
                npcTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        npcPlayChess();
                    }
                }, Integer.parseInt(ResManager.getProperties().getProperty("DelayOfPlayer_Player")), Integer.parseInt(ResManager.getProperties().getProperty("DelayOfPlayer_Player")));
            }
        });
    }

    public void npcPlayChess() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    return;
                }
                int max = Math.abs(coordinateArr[0][0]);
                ArrayList<int[]> maxArr = new ArrayList<>();
                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        if (Math.abs(coordinateArr[i][j]) != 1 && Math.abs(coordinateArr[i][j]) > max) {
                            max = Math.abs(coordinateArr[i][j]);
                        }
                    }
                }
                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        if (Math.abs(coordinateArr[i][j]) == max) {
                            maxArr.add(new int[]{i, j});
                        }
                    }
                }
                Random random = new Random();
                int ran = random.nextInt(maxArr.size());
                int chessX = maxArr.get(ran)[0];
                int chessY = maxArr.get(ran)[1];

                if (Math.abs(coordinateArr[chessX][chessY]) == 1) {
                    npcTimer.cancel();
                    Npc_Npc.this.isGameOver = true;
                    progressBars[0].setProgress(1);
                    progressBars[1].setProgress(1);
                    cheTimer.cancel();
                    startTimer.cancel();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("游戏结束");
                    alert.setHeaderText("平局");
                    alert.showAndWait();
                    return;
                }

                coordinateArr[chessX][chessY] = chessArrayList.size() % 2 == 0 ? 1 : -1;
                new Chess(checkerboard, chessArrayList, chessX, chessY);
                updateCheLables(chessArrayList.get(chessArrayList.size() - 1));
                addTargetIco(chessArrayList.size() - 1);
                ResManager.playFallSound();

                isWin(chessX, chessY);

                getWeight(1);
                showWeight();

                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        System.out.printf("%-4d", coordinateArr[j][i]);
                    }
                    System.out.println();
                }
                System.out.println(chessArrayList);
                cheCountdown();
            }
        });
    }

    public void getWeight(int num) {//获取权重
        for (int chessX = 0; chessX < coordinateArr.length; chessX++) {
            for (int chessY = 0; chessY < coordinateArr[0].length; chessY++) {
                if (coordinateArr[chessX][chessY] == 1 || coordinateArr[chessX][chessY] == -1) {
                    continue;
                }
                /*
                num值传递1或-1
                coordinateArr是存放int数字的二维数组，二维数组中，玩家的数据为正数，人机的数据为负数，棋子为1和-1标记，权重为非0，1，-1的数据
                chessX和chessY是数组的索引
                xr、xl等为该方向连成串的棋子数
                 */
                for (int i = 0; i < 2; i++) {
                    int xr = 0;//x方向向右
                    int xl = 0;//x方向向左
                    int xa;//x方向总数
                    int yr = 0;
                    int yl = 0;
                    int ya;
                    int xyr = 0;
                    int xyl = 0;
                    int xya;
                    int yxr = 0;
                    int yxl = 0;
                    int yxa;

                    //region x方向
                    while (chessX + xr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + xr + 1][chessY] == num) {
                        xr++;
                    }
                    while (chessX - xl - 1 >= 0 && coordinateArr[chessX - xl - 1][chessY] == num) {
                        xl++;
                    }
                    xa = xr + xl;
                    //endregion

                    //region y方向
                    while (chessY + yr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX][chessY + yr + 1] == num) {
                        yr++;
                    }
                    while (chessY - yl - 1 >= 0 && coordinateArr[chessX][chessY - yl - 1] == num) {
                        yl++;
                    }
                    ya = yr + yl;
                    //endregion

                    //region 正斜杠方向
                    while (chessX + xyr + 1 < Checkerboard.getCHELINENUM() && chessY - xyr - 1 >= 0 && coordinateArr[chessX + xyr + 1][chessY - xyr - 1] == num) {
                        xyr++;
                    }
                    while (chessX - xyl - 1 >= 0 && chessY + xyl + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX - xyl - 1][chessY + xyl + 1] == num) {
                        xyl++;
                    }
                    xya = xyr + xyl;
                    //endregion

                    //region 反斜杠方向
                    while (chessY + yxr + 1 < Checkerboard.getCHELINENUM() && chessX + yxr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + yxr + 1][chessY + yxr + 1] == num) {
                        yxr++;
                    }
                    while (chessY - yxl - 1 >= 0 && chessX - yxl - 1 >= 0 && coordinateArr[chessX - yxl - 1][chessY - yxl - 1] == num) {
                        yxl++;
                    }
                    yxa = yxr + yxl;
                    //endregion

                    //region 确定权重

                    //region 一般权重 初步赋值
//                    boolean xr2 = (chessX + 2 < Checkerboard.getCHELINENUM()) && (coordinateArr[chessX + 2][chessY] == num);//当【chessX，chessY】点的右边两点为同色棋子时为true
//                    boolean xl2 = (chessX - 2 >= 0) && (coordinateArr[chessX - 2][chessY]) == num;
//                    boolean yr2 = (chessY + 2 < Checkerboard.getCHELINENUM()) && (coordinateArr[chessX][chessY + 2] == num);
//                    boolean yl2 = (chessY - 2 >= 0) && (coordinateArr[chessX][chessY - 2]) == num;
//                    boolean xyr2 = (chessX + 2 < Checkerboard.getCHELINENUM()) && (chessY - 2 >= 0) && coordinateArr[chessX + 2][chessY - 2] == num;
//                    boolean xyl2 = (chessX - 2 >= 0) && (chessY + 2 < Checkerboard.getCHELINENUM()) && coordinateArr[chessX - 2][chessY + 2] == num;
//                    boolean yxr2 = (chessX + 2 < Checkerboard.getCHELINENUM()) && (chessY + 2 < Checkerboard.getCHELINENUM()) && coordinateArr[chessX + 2][chessY + 2] == num;
//                    boolean yxl2 = (chessX - 2 >= 0) && (chessY - 2 >= 0) && coordinateArr[chessX - 2][chessY - 2] == num;
//
//                    boolean xr3 = (chessX + 3 < Checkerboard.getCHELINENUM()) && (coordinateArr[chessX + 3][chessY] == num);//当【chessX，chessY】点的右边三点为同色棋子时为true
//                    boolean xl3 = (chessX - 3 >= 0) && (coordinateArr[chessX - 3][chessY]) == num;
//                    boolean yr3 = (chessY + 3 < Checkerboard.getCHELINENUM()) && (coordinateArr[chessX][chessY + 3] == num);
//                    boolean yl3 = (chessY - 3 >= 0) && (coordinateArr[chessX][chessY - 3]) == num;
//                    boolean xyr3 = (chessX + 3 < Checkerboard.getCHELINENUM()) && (chessY - 3 >= 0) && coordinateArr[chessX + 3][chessY - 3] == num;
//                    boolean xyl3 = (chessX - 3 >= 0) && (chessY + 3 < Checkerboard.getCHELINENUM()) && coordinateArr[chessX - 3][chessY + 3] == num;
//                    boolean yxr3 = (chessX + 3 < Checkerboard.getCHELINENUM()) && (chessY + 3 < Checkerboard.getCHELINENUM()) && coordinateArr[chessX + 3][chessY + 3] == num;
//                    boolean yxl3 = (chessX - 3 >= 0) && (chessY - 3 >= 0) && coordinateArr[chessX - 3][chessY - 3] == num;
                    int thisWeight = (xa + ya + xya + yxa) * 10 * num;
                    //endregion

                    //region 特别情况权重 重新赋值
                    //region 判断各个方向拦截情况
                    boolean xboolean = (chessX + xr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + xr + 1][chessY] != -num) && (chessX - xl - 1 >= 0 && coordinateArr[chessX - xl - 1][chessY] != -num);//当某棋子x方向上形成的连珠两端都无拦截时为true(无对方棋子和棋盘边界)
                    boolean yboolean = (chessY + yr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX][chessY + yr + 1] != -num) && (chessY - yl - 1 >= 0 && coordinateArr[chessX][chessY - yl - 1] != -num);
                    boolean xyboolean = (chessX + xyr + 1 < Checkerboard.getCHELINENUM() && chessY - xyr - 1 >= 0 && coordinateArr[chessX + xyr + 1][chessY - xyr - 1] != -num) && (chessX - xyl - 1 >= 0 && chessY + xyl + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX - xyl - 1][chessY + xyl + 1] != -num);
                    boolean yxboolean = (chessX + yxr + 1 < Checkerboard.getCHELINENUM() && chessY + yxr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + yxr + 1][chessY + yxr + 1] != -num) && (chessY - yxl - 1 >= 0 && chessX - yxl - 1 >= 0 && coordinateArr[chessX - yxl - 1][chessY - yxl - 1] != -num);

                    boolean xrboolean = (chessX + xr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + xr + 1][chessY] != -num);//当某棋子x方向上形成的连珠右端无拦截时为true(无对方棋子和棋盘边界)
                    boolean yrboolean = (chessY + yr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX][chessY + yr + 1] != -num);
                    boolean xyrboolean = (chessX + xyr + 1 < Checkerboard.getCHELINENUM() && chessY - xyr - 1 >= 0 && coordinateArr[chessX + xyr + 1][chessY - xyr - 1] != -num);
                    boolean yxrboolean = (chessX + yxr + 1 < Checkerboard.getCHELINENUM() && chessY + yxr + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX + yxr + 1][chessY + yxr + 1] != -num);

                    boolean xlboolean = (chessX - xl - 1 >= 0 && coordinateArr[chessX - xl - 1][chessY] != -num);//当某棋子x方向上形成的连珠左端无拦截时为true(无对方棋子和棋盘边界)
                    boolean ylboolean = (chessY - yl - 1 >= 0 && coordinateArr[chessX][chessY - yl - 1] != -num);
                    boolean xylboolean = (chessX - xyl - 1 >= 0 && chessY + xyl + 1 < Checkerboard.getCHELINENUM() && coordinateArr[chessX - xyl - 1][chessY + xyl + 1] != -num);
                    boolean yxlboolean = (chessY - yxl - 1 >= 0 && chessX - yxl - 1 >= 0 && coordinateArr[chessX - yxl - 1][chessY - yxl - 1] != -num);
                    //endregion
//                    if (//死四(只有一端被拦截的四)
//                            !xboolean && (xrboolean || xlboolean) && (xa == 3) ||
//                                    !yboolean && (yrboolean || ylboolean) && (ya == 3) ||
//                                    !xyboolean && (xyrboolean || xylboolean) && (xya == 3) ||
//                                    !yxboolean && (yxrboolean || yxlboolean) && (yxa == 3)
//                    ) {
//                        thisWeight += 10;
//                    }
                    if (//形成活四
                            (xboolean && xa == 3) ||//当该位置x方向的棋子数目为3，且连成5子的尽头都没有被拦截(对方棋子或棋盘尽头)
                                    (yboolean && ya == 3) ||
                                    (xyboolean && xya == 3) ||
                                    (yxboolean && yxa == 3)
                    ) {
                        thisWeight = 300 * num;
                    }

                    if (//形成双活三
                            xboolean && yboolean && (xa == 2 && ya == 2) ||
                                    xboolean && yxboolean && (xa == 2 && yxa == 2) ||
                                    xboolean && xyboolean && (xa == 2 && xya == 2) ||
                                    yboolean && yxboolean && (ya == 2 && yxa == 2) ||
                                    yboolean && xyboolean && (ya == 2 && xya == 2) ||
                                    xyboolean && yxboolean && (xya == 2 && yxa == 2)
                    ) {
                        thisWeight = 300 * num;
                    }

                    if (//形成双四(形成的两条四每条都至少有一端活)
                            (xrboolean || xlboolean) && (yrboolean || ylboolean) && (xa == 3 && ya == 3) ||
                                    (xrboolean || xlboolean) && (yxrboolean || yxlboolean) && (xa == 3 && yxa == 3) ||
                                    (xrboolean || xlboolean) && (xyrboolean || xylboolean) && (xa == 3 && xya == 3) ||
                                    (yrboolean || ylboolean) && (yxrboolean || yxlboolean) && (ya == 3 && yxa == 3) ||
                                    (yrboolean || ylboolean) && (xyrboolean || xylboolean) && (ya == 3 && xya == 3) ||
                                    (xyrboolean || xylboolean) && (yxrboolean || yxlboolean) && (xya == 3 && yxa == 3)
                    ) {
                        thisWeight = 310 * num;
                    }

                    if (//形成冲四活三(三为双活，四有一端为活)
                            (xrboolean || xlboolean) && yboolean && (xa == 3 && ya == 2) ||
                                    (xrboolean || xlboolean) && yxboolean && (xa == 3 && yxa == 2) ||
                                    (xrboolean || xlboolean) && xyboolean && (xa == 3 && xya == 2) ||
                                    (yrboolean || ylboolean) && yxboolean && (ya == 3 && yxa == 2) ||
                                    (yrboolean || ylboolean) && xyboolean && (ya == 3 && xya == 2) ||
                                    (xyrboolean || xylboolean) && yxboolean && (xya == 3 && yxa == 2) ||

                                    xboolean && (yrboolean || ylboolean) && (xa == 2 && ya == 3) ||
                                    xboolean && (yxrboolean || yxlboolean) && (xa == 2 && yxa == 3) ||
                                    xboolean && (xyrboolean || xylboolean) && (xa == 2 && xya == 3) ||
                                    yboolean && (yxrboolean || yxlboolean) && (ya == 2 && yxa == 3) ||
                                    yboolean && (xyrboolean || xylboolean) && (ya == 2 && xya == 3) ||
                                    xyboolean && (yxrboolean || yxlboolean) && (xya == 2 && yxa == 3)
                    ) {
                        thisWeight = 310 * num;
                    }

                    if (xa >= 4 || ya >= 4 || xya >= 4 || yxa >= 4) {//成五
                        thisWeight = 320 * num;
                    }
                    //endregion

                    //region 权重的赋值
                    if (i == 0) {
                        coordinateArr[chessX][chessY] = thisWeight;
                    } else {//权重的覆盖
                        if (Math.abs(thisWeight) > Math.abs(coordinateArr[chessX][chessY])) {
                            coordinateArr[chessX][chessY] = thisWeight - num;
                        }
                    }
                    //endregion
                    //endregion
                    num = -num;
                }
            }
        }
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

    public void isGameOver(boolean isGameOver) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    Npc_Npc.this.isGameOver = true;
                    progressBars[0].setProgress(1);
                    progressBars[1].setProgress(1);
                    cheTimer.cancel();
                    startTimer.cancel();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("游戏结束");
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
                    Npc_Npc.this.isGameOver = false;
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

    public void setShowWeight() {
        showWeightBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (showWeightIsOpen) {
                    showWeightIsOpen = false;
                    showWeightBtn.setText("开启权重显示");
                } else {
                    showWeightIsOpen = true;
                    showWeightBtn.setText("关闭权重显示");
                }
            }
        });
    }

    public void removeChess() {
        regretBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isGameOver(false);
                chessArrayList.get(chessArrayList.size() - 1).removeChessCircle();
                coordinateArr[chessArrayList.get(chessArrayList.size() - 1).getChessX()][chessArrayList.get(chessArrayList.size() - 1).getChessY()] = 0;
                chessArrayList.remove(chessArrayList.size() - 1);

                for (int i = 0; i < coordinateArr.length; i++) {
                    for (int j = 0; j < coordinateArr[0].length; j++) {
                        if (Math.abs(coordinateArr[i][j]) != 1) {
                            coordinateArr[i][j] = 0;
                        }
                    }
                }
                addTargetIco(chessArrayList.size() - 1);
                if (chessArrayList.size() == 0) {
                    updateCheLables();
                    startTimer();
                } else {
                    updateCheLables(chessArrayList.get(chessArrayList.size() - 1));
                    updateCheLables(chessArrayList.get(chessArrayList.size() - 2));
                    ContinueStartTimer();
                }
                cheCountdown();

            }
        });
    }

    public void clearChess() {

        restartBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                npcTimer.cancel();
                pane.getChildren().removeIf(new Predicate<Node>() {
                    @Override
                    public boolean test(Node node) {
                        if (node instanceof ChessCircle && ((ChessCircle) node).getCount() == -1) {
                            return false;
                        }
                        return node instanceof ChessCircle || node instanceof TargetIco || node instanceof Text;
                    }
                });
                chessArrayList.clear();
                coordinateArr = new int[Checkerboard.getCHELINENUM()][Checkerboard.getCHELINENUM()];
                isGameOver(false);
                updateCheLables();
                startTimer();
                cheCountdown();
                begin();
            }
        });

    }

}
