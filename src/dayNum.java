import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class dayNum{
    Text  [] arr ;
    double [] dif ;

    dayNum(Font font){
        arr = new Text[3];
    }
    dayNum(int [] day, Font font){
        arr = new Text[3];
        dif = new double[3];
        setFormat(font);

        if(day[0] > day[6]){
            String str [] = new String[2];
            int i = 0;
            for(; i < 6; ++i)
                if(day[i] <= 1) break;
                else str[0] += next(i);
            arr[0].setText(str[0]);
            for(; i < 6; ++i)
                str[1] += next(i);
            arr[1].setText(str[1]);
            arr[2].setText(day[6]+"");


        }else if(day[6] < 7){

        }else{

        }
    }
    String next(int n){
        if((n+"").length() == 2  ){
            return n + "        ";
        }
        return n + "         ";
    }
    void show(Pane pane){

    }
    void setFormat(Font font){
        for(int i = 0; i < arr.length; ++i){
            arr[i].setFont(font);
            arr[i].setStyle("-fx-font-size: 22.8px;");
        }
    }
    void setXY(double x, double y){
        arr[0].setX(x);
        arr[0].setY(y);
    }
}