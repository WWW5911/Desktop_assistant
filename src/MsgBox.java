import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.beans.EventHandler;


import javafx.animation.FadeTransition;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MsgBox {
    MyImage background, msgBox;
    MyButton yes, no;
    BorderPane pane;
    Pane depend;
    Text text;
    double scale, picW;
    FadeTransition fadeTransition;
    MsgBox(){
        init();
    }
    MsgBox(String msg){
        init();
        setMsg(msg);
    }
    MsgBox(String msg, int type){
        init();
        setMsg(msg);
        setButtonType(type);
        yes.show(pane);
        if(no != null) no.show(pane);
    }
    void setMsg(String msg){
        if(text == null){
             text = new Text(msg);
             text.setFont(Main.font);
             text.setTextAlignment(TextAlignment.CENTER);
             text.setStyle("-fx-font-size: 22.8px;");
             pane.getChildren().add(text);
             text.setWrappingWidth(picW-20*scale);
        }
        else text.setText(msg);
        text.setLayoutX((Main.originW-picW)/2 + 20*scale/2 );
        text.setLayoutY((Main.originH-400*scale)/2 + 100*scale);
    }


    void init(){
        pane = new BorderPane();
        background = new MyImage("25background.png");
        background.setOpacity(0.5);
        msgBox = new MyImage("MsgBox.png", new Point(0,160));
        background.show(pane);
        scale = 0.5;
        picW = 1050 * scale;
        setScale(scale);
        msgBox.show(pane);

        fadeTransition = new FadeTransition(Duration.millis(150), pane);
        pane.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.SECONDARY) hide();
        });
    }
    void setScale(double s){
        scale = s;
        picW = 1050 * scale;
        msgBox.setWidth(1280 * scale);
        msgBox.setXY( getCenterX(), getCenterY() ) ;
    }
    void setButtonType(int type){
        if(type == 1){
            yes = new MyButton("yes", "yes_btn.png",100);
            yes.setButtonType(1);
            yes.setXY(Main.originW/2-picW*0.3, getCenterY()+300*scale);
            no = new MyButton("yes", "no_btn.png",100);
            no.setButtonType(1);
            no.setXY(Main.originW/2+picW*0.3 - 100, getCenterY()+300*scale);
            no.btn.setOnMouseClicked(e->{hide();});


        }
        else{
            yes = new MyButton("ok", "ok_btn.png" );
            yes.setXY(getCenterX(), getCenterY()+300*scale);
        } 
        
    }



    double getCenterX(){
        return (Main.originW-1280*scale)/2;
    }
    double getCenterY(){
        return (Main.originH-400*scale)/2 ;
    }

    void show(Pane root){
        fadeTransition.setOnFinished(e->{});
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        root.getChildren().add(pane);
        depend = root;
        
    }
    void hide(){
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(e -> depend.getChildren().remove(pane) );
        fadeTransition.play();

        
    }
}
