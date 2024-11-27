import java.lang.Thread.State;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.Button;


public class TextCanvas {
    Text text;
    double maxW, maxH, initialX, moveX, textWidth;
    int id;
    Pane pane ;
    MyButton mbtn;
    Button btn;
    Thread thread;
    Boolean flag, flag2; //falg2 means isAllday
    TextCanvas(double W, double H){
        maxW = W;
        maxH = H;
        initialX = 3.5;
        flag = false;
        flag2 = false;
        moveX = initialX;
        pane = new BorderPane();
        text = new Text();
        Rectangle empty = new Rectangle(0, 0, 0, 0);;
        mbtn = new MyButton("", "transparent.png", new double[]{W, H}, new Point(W/2, H/2-18) );
        btn = mbtn.btn;
        pane.getChildren().add(empty);
        pane.getChildren().add(text);
        mbtn.show(pane);
        text.setStyle("-fx-font-size: 17px;");
        pane.setOpacity(0);
        text.setX(initialX);
        text.setY(0);
        textWidth = 0;

        
    }

    void drawBackground(int flag, String [] colors){
        pane.getChildren().remove(0);
        switch(flag){
            case 1:
                LinearGradient lg = new LinearGradient(0,0,1,0, true, CycleMethod.NO_CYCLE
                                , new Stop(0, webToColor(colors[0], 0.5)), new Stop(1, webToColor(colors[1], 0.4)) );
                Rectangle r1 = new Rectangle(0, 4, maxW-1, maxH-4);
                r1.setArcHeight(10);
                r1.setArcWidth(10);
                r1.setFill(lg);
                r1.setY(-18);
                pane.getChildren().add(0,r1);
                break;
            default:
                break;
        }
    }
    // void setEventBtn(EventUI eui){
    //     btn.setOnMouseClicked(e->{
    //     });
    // }
    double getY(){
        return pane.getLayoutY();
    }

    double getEndY(){
        return pane.getLayoutY()+maxH;
    }
    void setTextY(double Y){
        text.setLayoutY(Y);
    }
    void setFlag2(boolean f){
        flag2 = f;
    }
    double getLimitTextY(){
        return maxH-text.getLayoutBounds().getHeight();
    }

    void setText(String str){

        if(str.length() == 0)
            pane.setOpacity(0);
        else pane.setOpacity(1);
        text.setText(str);
        Rectangle clip = new Rectangle(0,0,maxW-3,maxH-5);
        clip.setY(-18);
        text.setClip(clip); 
        
    }
    void startAnimation(){
        textWidth = text.getLayoutBounds().getWidth();
        if(textWidth > maxW){
            startThread();
        }
    }

    void stopAnimation(){
        if(textWidth > maxW){
            stopThread();
        }
    }
    

    void startThread(){
        
        if(!flag){
            moveX = initialX;
            while(thread == null)
            thread = new Thread(){
                @Override
                public void run(){
                    while(flag)
                    try {
               //         System.out.println(moveX + " " + flag);
                        moveX -= 0.5;
                        if(moveX < (textWidth+2 )*-1){
                            moveX = maxW;
                        }
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run(){
                                text.setX(moveX);
                            }
                        } );
                        sleep(16L);
                    } catch (Exception e) {
                  //      e.printStackTrace(); 
                        this.interrupt();
                    }
                    
                }
            };
            if(thread != null)
                thread.start();
            flag = true;
        }
    }
    void stopThread(){
        if(flag){
            text.setX(initialX);
            thread.interrupt();
            thread = null;
            moveX = initialX;
            flag = false;
        }
    }

    Color webToColor(String str, double openness){
        if(str.charAt(0) == '#') str = str.substring(1);
        str = str.toUpperCase();
        int r = (str.charAt(0) < 58 ? str.charAt(0)-48 : str.charAt(0)-55 )*16 + (str.charAt(1) < 58 ? str.charAt(1)-48 : str.charAt(1)-55 ) ;
        int g = (str.charAt(2) < 58 ? str.charAt(2)-48 : str.charAt(2)-55 )*16 + (str.charAt(3) < 58 ? str.charAt(3)-48 : str.charAt(3)-55 );
        int b = (str.charAt(4) < 58 ? str.charAt(4)-48 : str.charAt(4)-55 )*16 + (str.charAt(5) < 58 ? str.charAt(5)-48 : str.charAt(5)-55 );
        return Color.rgb(r, g, b, openness);
    }
    void show(Pane root){
        root.getChildren().add(pane);
    }
    void leave(Pane root){
        root.getChildren().remove(pane);
    }
    void hide(Pane root){
        root.getChildren().remove(pane);
    }
    void setXY(double x, double y){
        pane.setLayoutX(x);
        pane.setLayoutY(y);
    }
    void setID(int idd){
        id = idd;
    }
}
