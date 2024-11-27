import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.animation.PathTransition;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.util.Duration;

public class Schedule{
    int year, month, day;
    MyImage background, black, headerImg, straightLine;
    Pane pane, header, fuct;
    Text [] clocktext;
    Text headerText;
    Font font;
    double posiX, posiY, speed, lastposi, dragposi, acceleration;
    Point lastMousePosi;
    boolean flag = false, isAnimStart, RbtnFlag;
    Rectangle textBack;
    Thread thread;
    TextCanvas [] eventList;
    Pane depend;
    MyButton back, addNew;
    EventUI eui;
    MyCalendar lastPage;
    EventCenter showingEC;

    
    Schedule(){
        pane = new BorderPane();
        header = new BorderPane();
        fuct = new BorderPane();
        init();
    } 
    Schedule(String daystr){ // 2000-01-02
        pane = new BorderPane();
        header = new BorderPane();
        fuct = new BorderPane();
        setDate(daystr);
        init();
    }
    Schedule(MyCalendar depend){ // 2000-01-02
        pane = new BorderPane();
        header = new BorderPane();
        fuct = new BorderPane();
        lastPage = depend;
        init();
    }
    Schedule(String daystr, Font fontt){ // 2000-01-02
        pane = new BorderPane();
        header = new BorderPane();
        fuct = new BorderPane();
        setDate(daystr);
        font = fontt;
        init();
    }
    



    void init(){  // System.getProperty("user.dir")
        posiY = 0;
        posiX = 0;
        speed = 0.6;
        isAnimStart = false;
        back =  new MyButton("123", "back_btn.png", 100, 45, new Point(70,610));
        back.addToPane(fuct);
        back.setButtonType(1);
        back.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                leave();
            }
        });
        addNew = new MyButton("123", "addNew_btn.png", 100, 45, new Point(0,540));
        addNew.addToPane(fuct);
        addNew.setButtonType(1);
        addNew.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                addNewEvent();

            }
        });

        black = new MyImage( "50background.png");
        black.setSize(new double[]{1280,1920});
        black.show(pane);

        background = new MyImage( "letter30.png" );
        background.show(pane);
        straightLine = new MyImage("straight line.png", new Point(212, 260));
        straightLine.show(pane);
        textBack = new Rectangle(0, 250, 100 ,1150);
        LinearGradient lg = new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE
                    , new Stop(0, webToColor("#FFFCEB", 0.5)), new Stop(0.9, webToColor("#FFFCEB", 0.5)) , new Stop(1, webToColor("#FFFCEB", 0)) );
        textBack.setFill(lg);
        textBack.setX(90);
        pane.getChildren().add(textBack);
        headerImg = new MyImage( "header.png" );
        headerImg.show(header);
        headerImg.setY(64);

        headerText = new Text(year +"\n" + month + "/" + day);
        headerText.setStyle("-fx-font-size: 28px;");
        headerText.setTextAlignment(TextAlignment.RIGHT);
        headerText.setFill(Color.web("#3D1515"));
        header.getChildren().add(headerText);
        headerText.setLayoutY(128);
        headerText.setLayoutX(120);
        clocktext = new Text[24];
            for(int i = 1; i < 24; ++i){
                String str = "";
                if( i < 10)  str += "0";
                str += i + " : 00";
                clocktext[i] = new Text(str);
                clocktext[i].setFont(font);
                clocktext[i].setStyle("-fx-font-size: 20px;");
                clocktext[i].setY(250 + i*50);
                clocktext[i].setX(110 );
                pane.getChildren().add(clocktext[i]);
            }
        setPaneMouseEvent(pane);
        setPaneMouseEvent(fuct);
        setPaneMouseEvent(header);


    }

    void setPaneMouseEvent(Pane p){

        p.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.SECONDARY){
                leave();
                RbtnFlag = true;
            }
        });

        p.setOnMouseDragged(e->
        { 
            if(lastposi != 0.00001 && Math.signum(lastposi - e.getY())  == Math.signum(dragposi) ){
                dragposi = lastposi - e.getY();
                scrollPane(dragposi + dragposi);
            }
            dragposi = lastposi - e.getY();
            lastposi = e.getY() ; 
        
        });
        p.setOnMouseReleased(e->{ 
            lastposi = 0.00001;
        });

        
        p.setOnScroll(e->{
            scrollPane(e.getDeltaY()*speed*-1);
        } );
    }


    void scrollPane(double value){
        posiY -= value;
        if(posiY > 0) posiY = 0;
        if(posiY < -1128) posiY = -1128;
        pane.setLayoutY(posiY);
        if(eventList != null)
        for(int i = 0; i < eventList.length; ++i){
            if(eventList[i] != null && !eventList[i].flag2){
                double tmp = (-1*posiY+162) - eventList[i].getY();
                if( tmp > 0  && tmp < eventList[i].getLimitTextY() ){ // && tmp < eventList[i].getEndY()
                    eventList[i].setTextY( tmp );
                }
            }
        }
        double tmp = posiY;
        if(tmp < -68 ) tmp = -68;
        if(tmp > 0 ) tmp = 0;
        header.setLayoutY(tmp);
    }
    void setDate(String daystr){
        year = Integer.parseInt(daystr.split("-")[0]);
        month = Integer.parseInt(daystr.split("-")[1]);
        day = Integer.parseInt(daystr.split("-")[2]);
    }
    void leave(){
        if(lastPage != null) lastPage.reload();
        try{
            depend.getChildren().remove(pane);
            depend.getChildren().remove(header);
            depend.getChildren().remove(fuct);
            for(int i = 0; i < eventList.length; ++i){  ////////////////////  事件表標題?????  外部顯示今天事件
                if(eventList[i] == null) {
                    System.out.println(i + "");
                    continue;
                }
    
                eventList[i].hide(pane);
                eventList[i].hide(header);
                eventList[i] = null;
            }
        }catch(Exception e){}

    }
    void addNewEvent(){
        createEui(false);
        eui.setMode(1);
        eui.show(depend);
    }

    void createEui(boolean isEmpty){
        if(eui != null){ 
            eui.animThread.interrupt();
            eui.animThread = null;
        }
        if(!isEmpty){
            eui = new EventUI(year, month, day);
        }else eui = new EventUI();

    }

    void load(EventCenter EC){
        JSONArray eventArr = EC.getDayData(day);   // index means start/end at that moment 0 means 0:00 1440 means 24:00(only works on endtime)
        setDate(EC.date + "-" + day);
        headerText.setText(year + "\n" + month + "/" + day);
        STmatrix SameTime = new STmatrix(1441);
        STmatrix [] channels ;
        eventList = new TextCanvas[eventArr.length()];
        int MaxEvent = 0, alldayCount = 0, showingAllDay = 0;
        ArrayList<timeLine> al = new ArrayList<timeLine>();
        double totalWidth = 800;
        for(int i = 0; i < eventArr.length(); ++i){
            if( eventArr.getJSONObject(i).getInt("Allday") == 1 ) {
                // add all day event to pane (need to consider multiple event)
                al.add( new timeLine(true, i) );
                ++alldayCount;
                continue;

            };
            int s = eventArr.getJSONObject(i).getInt("StartTime"), e = eventArr.getJSONObject(i).getInt("EndTime");
            for(int j = s; j < e; ++j){
                SameTime.addOne(j);
            }
            al.add( new timeLine(s, e, i) );
        }

        Collections.sort(al);
        SameTime.construct();
        MaxEvent = (int)SameTime.search(0, 1440);

        channels = new STmatrix[MaxEvent];
        for( int mm = 0; mm < MaxEvent; ++mm)
            channels[mm] = new STmatrix(1441);
        
        for( int i = 0; i < al.size(); ++i ){
            JSONObject event = eventArr.getJSONObject( al.get(i).index );
            if( al.get(i).allday) {
                double eventW = totalWidth/alldayCount;
                eventList[i] = new TextCanvas(eventW, 90 );
                eventList[i].setText( event.getString("Title") );
                eventList[i].text.setWrappingWidth(eventW);
                eventList[i].setID(al.get(i).index);
                eventList[i].setXY(250 + showingAllDay * eventW, 120);
                eventList[i].drawBackground(1, new String[]{"#123456", "#223456"} );
                eventList[i].show(header);
                eventList[i].setFlag2(true);
                ++showingAllDay;
                continue;
            }
            int s = event.getInt("StartTime"), e = event.getInt("EndTime");
            int channel = MaxEvent-1;
            int eventInSameTime =  (int)SameTime.search(s, e-1);
            for(int j = 0; j < MaxEvent-1; ++j){
                if(channels[j].getOrigin(s) == 0 && channels[j].getOrigin(e) ==0 && channels[j].search(s, e) == 0){
                    channel = j;
                    break;
                }
            }
            for(int j = s; j < e; ++j)
                channels[channel].add(j, totalWidth / eventInSameTime);
            channels[channel].construct();
            
         //   double posiX = channel * 800 / MaxEvent;
        //    double eventWidth = 800 - (800 / MaxEvent) * (MaxEvent - eventInSameTime) , eventHeigh = (e-s)/60*49.5 + (e-s)%60/60*49.5;
        //    double eventWidth = 800 - channel * 800/eventInSameTime, eventHeigh = (e-s)/60*49.5 + (e-s)%60/60*49.5;
            double eventWidth = totalWidth / eventInSameTime , eventHeigh = (double)(e-s)/60*49.5;  // (e-s)/60*49.5 + (e-s)%60/60*49.5

            double posiX = 0;
            for(int k = 0; k < channel; ++k){
                posiX += channels[k].getOrigin(s);
            }
            if(eventInSameTime-1 == channel) 
                eventWidth = totalWidth - posiX;
            eventList[i] = new TextCanvas(eventWidth, eventHeigh );
            eventList[i].setText( event.getString("Title") );
            eventList[i].text.setWrappingWidth(eventWidth);
            eventList[i].setID(al.get(i).index);
            eventList[i].setXY(250 + posiX, 270 + (double)s/60*49.5 );  //  s/60*49.5 + s%60/60*49.5
            eventList[i].drawBackground(1, new String[]{"#123456", "#223456"} );
            eventList[i].show(pane);
        }

        createEui(true);
        setEventBtn(EC);
    }
    
    void setEventBtn(EventCenter EC){
        eui.setEventCenter(EC);
        for(int i = 0; i < eventList.length; ++i){
            if(eventList[i] == null) {
                System.out.println(i + "");
                continue;
            }
            final int index = eventList[i].id;
            eventList[i].btn.setOnMousePressed(e->{
                if(e.getButton() != MouseButton.SECONDARY){
                    lastMousePosi = new Point(e.getX(), e.getY());
                }
                else if(e.getButton() == MouseButton.SECONDARY){
                    leave();
                    RbtnFlag = true;
                }
            });

            eventList[i].btn.setOnMouseDragged(e->
            { 
                if(lastposi != 0.00001 && Math.signum(lastposi - e.getY())  == Math.signum(dragposi) ){
                    dragposi = lastposi - e.getY();
                    scrollPane(dragposi + dragposi);
                }
                dragposi = lastposi - e.getY();
                lastposi = e.getY() ; 
            
            });
            eventList[i].btn.setOnMouseReleased(e->{ 
                lastposi = 0.00001;
                if(e.getButton() != MouseButton.SECONDARY &&( isAnimStart || lastMousePosi.distence(e.getX(), e.getY()) < 10 ) ){
                    try{
                        EC.load();
                    }catch(Exception eee) {System.out.println("載入讀取失敗");}
                    eui.setDate(year, month, day);
                    eui.load(day, index);
                //    leave();
                    eui.show(depend, 1);          
                }
                else if(e.getButton() == MouseButton.SECONDARY){
                    leave();
                    RbtnFlag = true;
                }
            });
    


        }
    }


    int Max(int i, int j){
        return i > j ? i : j;
    }

    void show(Pane root){
        RbtnFlag = false;
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY), minute = cal.get(Calendar.MINUTE);
        showPaneItem(root);
        anim(hour, minute);
    }
    void show(Pane root, int hour, int minute){
        showPaneItem(root);
        anim(hour, minute);
    }


    void showPaneItem(Pane root){
        posiY = 0;
        acceleration = 2;
        depend = root;
        root.getChildren().add(pane);
        root.getChildren().add(header);
        root.getChildren().add(fuct);
        fuct.setLayoutX(1100);
    }

    void anim(int hour, int minute){
        isAnimStart = true;
        double curTimeY = -(hour * 45 + minute / 60 * 45)-0.1;
        thread = new Thread(){
            @Override
            public void run(){
                if(acceleration != 2) acceleration = 2;
                while(posiY > curTimeY){
                    acceleration *= 1.07;
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            if(posiY - acceleration > curTimeY)
                                scrollPane(acceleration);
                            else{
                                scrollPane(posiY - curTimeY);
                            }
                        }
                    });
                    try{
                        sleep(16);
                    }catch(Exception e){ e.printStackTrace(); }
                }
                isAnimStart = false;
            }
        };
        thread.start();
    }
    
    void hide(Pane root){
        root.getChildren().remove(pane);
        root.getChildren().remove(header);
    }


    Color webToColor(String str, double openness){
        if(str.charAt(0) == '#') str = str.substring(1);
        str = str.toUpperCase();
        int r = (str.charAt(0) < 58 ? str.charAt(0)-48 : str.charAt(0)-55 )*16 + (str.charAt(1) < 58 ? str.charAt(1)-48 : str.charAt(1)-55 ) ;
        int g = (str.charAt(2) < 58 ? str.charAt(2)-48 : str.charAt(2)-55 )*16 + (str.charAt(3) < 58 ? str.charAt(3)-48 : str.charAt(3)-55 );
        int b = (str.charAt(4) < 58 ? str.charAt(4)-48 : str.charAt(4)-55 )*16 + (str.charAt(5) < 58 ? str.charAt(5)-48 : str.charAt(5)-55 );
        return Color.rgb(r, g, b, openness);
    }

}
class STmatrix{
    Boolean isMax;
    double [][] ST;
    int size;
    STmatrix(){}
    STmatrix(int size){
        this.size = size;
        ST = new double[size][(int)(Math.log10(1441) / Math.log10(2)+1)];
        isMax = true;
    }
    STmatrix(int size, Boolean maxx){
        ST = new double[size][(int)(Math.log10(1441) / Math.log10(2)+1)];
        isMax = maxx;
    }

    double compare(double i, double j){
        if(isMax)
            return i > j ? i : j;
        return i < j ? i : j;
    }

    void construct(){
        for(int j = 1; (1 << j) <= size; ++j)
            for(int i = 0; i + (1 << j) -1 < size; ++i){
                int point = (1 << (j - 1));
                ST[i][j] = compare(ST[i][j - 1], ST[i + point][j - 1]);
            }
    }
    double search(int l, int r){
        int k = 0;
        while (1 << (k + 1) <= r - l + 1) ++k;
        return compare(ST[l][k], ST[r - (1 << k) + 1][k]);
    }

    void addOne(int i){
        ++ST[i][0];
    }
    double getOrigin(int i){
        return ST[i][0];
    }
    
    void add(int index, double num){
        ST[index][0] += num;
    }


}


class timeLine implements Comparable<timeLine>{
    int s , e, index;
    Boolean allday;
    timeLine(int ss, int ee, int in){
        s = ss;
        e = ee;
        index = in;
        allday = false;
    }
    timeLine(Boolean isAllday, int in){
        allday = isAllday;
        index = in;
    }
    @Override
    public int compareTo(timeLine o){  // 時間開始早、延續長的擺第一
        if(s > o.s) return 1;
        if(s < o.s) return -1;
        if(e > o.e) return -1;
        if(e < o.e) return 1;
        return 0;
    }
}
