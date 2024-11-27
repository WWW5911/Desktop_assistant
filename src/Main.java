import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Random;

public class Main extends Application {

    static String directoryName = System.getProperty("user.dir"), resfolder = "resources";
    static int originW = 1280, originH = 720;
    static Font font;
    static MyCalendar mc;
    static MyTimer mtimer;

    @Override
    public void start(Stage stage) throws Exception {
        font = Font.loadFont( EnDecoder.ReadEncodeFile( directoryName +"\\"+ resfolder + "\\Font\\HanaMinA.moe") , 26);
        mc = new MyCalendar(font);

        Pane root = new BorderPane();
        Scene scene = new Scene(root, originW, originH);
        MyImage bg = new MyImage("bg01.jpg", 1280, new Point(0,-100));
        bg.addToPane(root);

        MyImage chara = new MyImage("chara.png",  new Point(getCenter()[0]-300,120));
        chara.addToPane(root);

        MyImage chat = new MyImage("chat07.png") ; // , new Point(280,480)
        chat.addToPane(root);
        Text t = new Text (220, 570, "　　「凌月丰」\n今天還有很多事情還沒做哦，休息完救做事吧！");
        t.setFont(font);
        root.getChildren().add(t);


        MyImage edge01 = new MyImage("edge01.png", new Point(990,430));
        edge01.addToPane(root);

        

        MyButton calendar = new MyButton("123", "calendar_btn.png", 100, 45, new Point(1040,465));
        calendar.setButtonType(1);
        calendar.btn.setOnMouseClicked(e->{
            mc.setToday();
            mc.show(root);
        });

        MyButton todo = new MyButton("123", "todo_btn.png", 100, 45, new Point(1110,395));
        todo.setButtonType(1);
        MyButton design = new MyButton("123", "design_btn.png", 100, 45, new Point(1110,535));
        design.setButtonType(1);
        MyButton setting = new MyButton("123", "setting_btn.png", 100, 45, new Point(1040,605));
        setting.setButtonType(1);

        calendar.addToPane(root);
        setting.addToPane(root);
        todo.addToPane(root);
        design.addToPane(root);

      //  clouds cloud = new clouds(4, getCenterP(), root, originW, originH, 200, 200);
      //  cloud.show();
        
        mtimer = new MyTimer(){
            @Override
            void work(){
                if(current.get(Calendar.MINUTE) != lMinute){
                    if(!mc.Reloading) mc.reload();
                }
            }
        };
        mtimer.show(root);
        mtimer.setXY(1180, 700);

        



        stage.setTitle("JavaFX Canvas");
        stage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        stage.show();
    }
    public double[] getCenter(){
        return new double[]{originW/2, originH/2};
    }
    public Point getCenterP(){
        return new Point(originW/2, originH/2);
    }

    static void reloadSchedule(int year, int month, int day, boolean enterSchedule){
        mc.schedule.leave();
        mc.reload();
        if(enterSchedule) mc.showSchedule(year, month, day);
    }
    static void reloadSchedule(int year, int month, int day, int hour, int minute){
        mc.schedule.leave();
        mc.reload();
        mc.showSchedule(year, month, day, hour, minute);
    }





    public static void main(String[] args) throws Exception {

        launch();
    }


}

/*
        File folder1 = new File("./resource");
        String[] list1 = folder1.list();
        for (int i = 0; i < list1.length; i++) {
            EnDecoder.EncodeFile(list1[i]);
        }
*/
