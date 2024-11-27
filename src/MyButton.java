import java.io.File;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;

public class MyButton {
    Button btn;
    MyImage mImage;
    Image normal, enter;
    AudioClip enterSound;
    Boolean targed;
    int type; // 1 : functional button , 2: dayblock button
    double volume, defultV;

    MyButton(String name, String source){
        mImage = new MyImage(source);
        initial(name, 0, source);
    }

    MyButton(String name, String source, double width){
        mImage = new MyImage(source, width);
        initial(name, 0, source);
    }

    MyButton(String name, String source, double [] size){
        mImage = new MyImage(source, size);
        initial(name, 0, source);
    }
    MyButton(String name, String source, Point point){
        mImage = new MyImage(source);
        initial(name, 0, source);
        setXY(point.x, point.y);
    }
    MyButton(String name, String source, double width, Point point){
        mImage = new MyImage(source, width);
        initial(name, 0, source);
        setXY(point.x, point.y);
    }

    MyButton(String name, String source, double [] size, Point point){
        mImage = new MyImage(source, size);
        initial(name, 0, source);
        setXY(point.x, point.y);
    }
    MyButton(String name, String source, double width, double Rotate, Point point){
        mImage = new MyImage(source, width);
        initial(name, 0, source);
        setXY(point.x, point.y);
        btn.setRotate(Rotate);
    }

    boolean checkIsContain(Pane root){
        return root.getChildren().contains(btn);
    }
    private void initial(String name, int target, String source){
        defultV = 0.1;
        volume = defultV;
        source = source.substring(0, source.length()-4) + "_Enter" + source.substring(source.length()-4) ;
        try{
            if(source.substring(source.length()-3).contains("moe")){
                if(!EnDecoder.checkFile(source))
                    enter = new Image("");
                else enter = new Image( EnDecoder.ReadEncodeFile(source) ) ;
            }else{
                if(!source.contains(System.getProperty("user.dir")))
                    source = "file:" + System.getProperty("user.dir") + "\\resource\\" + source;
                enter = new Image(source);
            }
        }catch(Exception e){}
        btn = new Button(name, mImage.imageView);
        normal = mImage.getImage();
        targed = false;
        type = target;
        addEvent(target);
    }

    public void setXY(double X, double Y){
        btn.setLayoutX(X);
        btn.setLayoutY(Y);
    }
    public void setX(double X){
        btn.setLayoutX(X);
    }
    public void addToPane(Pane root){
        root.getChildren().add(btn);
    }
    public void show(Pane root){
        if(!root.getChildren().contains(btn))
            root.getChildren().add(btn);
    }
    public void hide(Pane root){
        root.getChildren().remove(btn);
    }
    
    public void setButtonType(int target){
        targed = true;
        type = target;
        addEvent(target);
    }
    public double getX(){
        return btn.getLayoutX();
    }
    void setEnterSoundVolume(double v){
        if(v < 0) volume = defultV;
        else volume = v;
    }
    void setImage(Image image){
        mImage.setImage(image);
    }
    public void addEvent(int target){
        
        if(target == 1){
            enterSound = new AudioClip(new File(System.getProperty("user.dir")+"\\"+ Main.resfolder + "\\tam_mouseIn01.wav").toURI().toString());
        }
        if(targed){
            if(target == 1){
                btn.addEventHandler(MouseEvent.MOUSE_ENTERED,
                    new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (type == 1){
                            mImage.setImage(enter);
                            enterSound.play(volume);
                        }
                    }
                });
                btn.addEventHandler(MouseEvent.MOUSE_EXITED,
                    new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (type == 1){
                            mImage.setImage(normal);
                        }
                        if(type == 2){

                        }
                    }
                }); 
            }
        }

    }
    
}
