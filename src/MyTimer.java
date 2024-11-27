import java.util.Calendar;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class MyTimer {
    Calendar current;
    Thread timerThread;
    int clock, lMinute;
    TextCanvas clockText;
    boolean countingFlag;


    MyTimer(){
        current = Calendar.getInstance();
        init();
        startTimer();
    }
    MyTimer(Calendar startTime){
        current = (Calendar)startTime.clone();
        init();
        startTimer();
    }
    void init(){
        clock = 1000;
        setPattern(1);
    }

    void startTimer(){
        countingFlag = true;
        timerThread = new Thread() {
            @Override
            public void run() {
                while (countingFlag) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lMinute = current.get(Calendar.MINUTE);
                            current = Calendar.getInstance();
                            clockText.setText(current.get(Calendar.HOUR_OF_DAY) + " : " + current.get(Calendar.MINUTE)
                                    + " : " + current.get(Calendar.SECOND));
                            work();
                        }
                    });
                    try {
                        sleep(clock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timerThread.start();
    }
    void work(){}

    void setPattern(int flag){
        if(flag == 1){
            clockText = new TextCanvas(100, 30);

        }
    }
    void show(Pane root){
        clockText.show(root);
    }
    void leave(Pane root){
        clockText.leave(root);
    }
    void setXY(double x, double y){
        clockText.setXY(x, y);
    }




}
