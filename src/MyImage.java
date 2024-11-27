import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MyImage {
    ImageView imageView;
    Boolean PreserveRatio;
    MyImage(String source){
        initial(source);
    }
    MyImage(String source, boolean PR){
        initial(source);
        PreserveRatio = PR;
    }
    MyImage(String source, double width){
        initial(source);
        setWidth(width);
        imageView.setPreserveRatio(PreserveRatio);
    }
    MyImage(String source, double [] size){
        initial(source);
        imageView.setPreserveRatio(false);
        setSize(size);
        
    }
    MyImage(String source, double width, Point point){
        initial(source);
        setWidth(width);
        imageView.setPreserveRatio(PreserveRatio);
        setXY(point.x, point.y);
    }

    MyImage(String source, Point point){
        initial(source);
        setXY(point.x, point.y);
    }
    MyImage(Image image){
        imageView = new ImageView();
        PreserveRatio = true;
        imageView.setImage(image);
    }
    private void initial(String source){
        imageView = new ImageView();
        PreserveRatio = true;
        Image image;
        if(source.substring(source.length()-3).contains("moe")){
            image = new Image( EnDecoder.ReadEncodeFile(source) ) ;
        }else{
            if(!source.contains(System.getProperty("user.dir")))
                source = "file:" + System.getProperty("user.dir") + "\\resource\\" + source;
            image = new Image(source);
        }
        

        imageView.setImage(image);
    }
    public void setImage(Image image){
        imageView.setImage(image);
    }
    public void setImage(String source){
        if(!source.contains(System.getProperty("user.dir")))
            source = "file:" + System.getProperty("user.dir") + "\\resource\\" + source;
        Image image = new Image(source);
        imageView.setImage(image);
    }
    public Image getImage(){
        return imageView.getImage();
    }
    public void setWidth(double width){
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(PreserveRatio);
    }
    public void setSize(double [] size){
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(size[0]);
        imageView.setFitHeight(size[1]);
    }
    public void setX(double X){
        imageView.setLayoutX(X);
    }
    public void setY(double Y){
        imageView.setLayoutY(Y);
    }
    public void setXY(double X, double Y){
        imageView.setLayoutX(X);
        imageView.setLayoutY(Y);
    }
    public void setOpacity(double op){
        imageView.setOpacity(op);
    }
    public double getWidth(){
        return imageView.getFitWidth();
    }
    public void addToPane(Pane root){
        root.getChildren().add(imageView);
    }
    public void show(Pane root){
        if(!root.getChildren().contains(imageView))
            root.getChildren().add(imageView);
    }
    public void hide(Pane root){
        root.getChildren().remove(imageView);
    }


}
