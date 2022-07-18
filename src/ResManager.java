
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.util.Properties;

public class ResManager {
    private static MediaPlayer bgm;
    private static MediaPlayer fallSound;
    private static MediaPlayer winSound;
    private static String proPath;
    private static final Properties properties;
    private static ImageView imageView;

    static {
        proPath = MainMenu.class.getClassLoader().getResource("resource/login_information.properties").getPath();
        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(proPath);
            InputStreamReader isr = new InputStreamReader(fis);
            properties.load(isr);
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView = new ImageView(MainMenu.class.getClassLoader().getResource("resource/"+properties.getProperty("BackGround")).toString());
        imageView.fitHeightProperty();
        imageView.fitWidthProperty();
        bgm = new MediaPlayer(
                new Media(Player_Npc.class.getClassLoader().getResource("resource/bgm.mp3").toString())
        );
        fallSound = new MediaPlayer(
                new Media(Player_Npc.class.getClassLoader().getResource("resource/落子声.mp3").toString())
        );
        winSound = new MediaPlayer(
                new Media(Player_Npc.class.getClassLoader().getResource("resource/胜利.mp3").toString())
        );
    }

    public static void playBGM() {
        bgm.setCycleCount(MediaPlayer.INDEFINITE);
        bgm.play();
    }

    public static void playFallSound() {
        fallSound.stop();
        fallSound.play();
    }

    public static void playWinSound() {
        winSound.stop();
        winSound.play();
    }

    public static String getProPath() {
        return proPath;
    }

    public static void setProPath(String proPath) {
        ResManager.proPath = proPath;
    }

    public static MediaPlayer getBgm() {
        return bgm;
    }

    public static void setBgm(MediaPlayer bgm) {
        ResManager.bgm = bgm;
    }

    public static MediaPlayer getFallSound() {
        return fallSound;
    }

    public static void setFallSound(MediaPlayer fallSound) {
        ResManager.fallSound = fallSound;
    }

    public static MediaPlayer getWinSound() {
        return winSound;
    }

    public static void setWinSound(MediaPlayer winSound) {
        ResManager.winSound = winSound;
    }

    public static ImageView getImageView() {
        return imageView;
    }

    public static void setImageView(ImageView imageView) {
        ResManager.imageView = imageView;
    }

    public static void setProperties(String key, String value) {
        properties.setProperty(key, value);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(proPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        try {
            properties.store(osw, "Used to store login information and settings");
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        try {
            FileInputStream fis = new FileInputStream(proPath);
            InputStreamReader isr = new InputStreamReader(fis);
            properties.load(isr);
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
