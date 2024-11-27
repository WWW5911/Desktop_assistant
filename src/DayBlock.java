import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Calendar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class DayBlock {
    Text T_date;
    TextCanvas [] TopEvent;
    int dateColorFlag, day, month; // 0 : work day, 1: weekend or holiday, 2 : not this Month 
    String dayString;
    double maxW, maxH;
    Pane pane ;
    MyButton btn;
    MyCalendar depend;

    DayBlock(Font font, Calendar calendar, int flag){
        init(font, calendar, flag);
    }

    DayBlock(Font font, Calendar calendar, int flag, MyCalendar mc){
        init(font, calendar, flag);
        setDepend(mc);
    }

    void init(Font font, Calendar calendar, int flag){
        T_date = new Text();
        setDateInfo(font, calendar.get(Calendar.DATE), flag);
        dayString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE);
        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH)+1;
        pane = new BorderPane();
        maxW = 115;
        maxH = 100;
        pane.setMaxSize(maxW, maxH);
        pane.getChildren().add(T_date);
        TopEvent = new TextCanvas[3];
        for(int i = 0; i < 3; ++i){
            TopEvent[i] = new TextCanvas(100,25);
            TopEvent[i].show(pane);
            TopEvent[i].setXY(0,20+i*20);
        }

    }
    void setDepend(MyCalendar mc){
        depend = mc;
    }
    
    void setButton(){
        btn = new MyButton("getIn", "transparent.png", new double[]{maxW-2, maxH-2}, new Point(-8, 26));
        btn.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                depend.showSchedule(dayString);
            }
        });
        btn.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.SECONDARY)
                depend.leave();
        });
        pane.getChildren().add(btn.btn);

//         btn.btn.setOnMouseEntered(e->{
//  //           System.out.println("start");
//             for(int i = 0; i < 3; ++i)
//                 TopEvent[i].startAnimation();
//         });
        btn.btn.setOnMouseMoved(e -> {
            double y = e.getY();
            if( y > -25 ){
                if(y < 0) {
                    TopEvent[0].startAnimation();
                    TopEvent[1].stopAnimation();
                    TopEvent[2].stopAnimation();
                }
                else if(y < 25){
                    TopEvent[1].startAnimation();
                    TopEvent[2].stopAnimation();
                    TopEvent[0].stopAnimation();
                }
                else{
                    TopEvent[2].startAnimation();
                    TopEvent[0].stopAnimation();
                    TopEvent[1].stopAnimation();
                }
            }else{
                for(int i = 0; i < 3; ++i)
                TopEvent[i].stopAnimation();
            }

        });
        btn.btn.setOnMouseExited(e->  {
  //          System.out.println("stop");
            for(int i = 0; i < 3; ++i)
                TopEvent[i].stopAnimation() ;
        });

    }
    void removeButton(){
        pane.getChildren().remove(btn.btn);
        btn = null;
    }

    public void setDateInfo(Font font, int day, int flag){
        dateColorFlag = flag;
        T_date.setFont(font);
        T_date.setText(day+"");
        T_date.setStyle("-fx-font-size: 22.8px;");
        if(dateColorFlag == 0)
            T_date.setFill(Color.web("#3D1515"));
        if(dateColorFlag == 1)
            T_date.setFill(Color.web("#FF5858"));
        if(dateColorFlag == 2)
            T_date.setFill(Color.web("#FFECEC"));
    }
    public void setXY(double x, double y){
        pane.setLayoutX(x);
        pane.setLayoutY(y);
    }
    public void show(Pane root){
        root.getChildren().add(pane);
    }
    public void hide(Pane root){
        root.getChildren().remove(pane);
    }

    
}


