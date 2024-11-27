import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

 // 先輸入標題在輸入時間會把標題給清掉

public class EventUI {
    EventCenter EC;
    MyImage bg, bg2, bgComma, fromTo, errorImage;
    MyButton AllDay, Save, Add, editbtn, Cancel, backbtn, deletebtn, copybtn, morebtn, completeBtn;
    Pane pane, depend;
    ArrayList<MyButton> moreFuct;
    TextField title, fromHour, fromMinute, toHour, toMinute;
    TextArea context, ps;
    Boolean isAllDay, Error, flagg = false, edited, editable, moreFuctFlag, MTFlag, animThreadFlag, soundFlag, ifEnterSchedule, iscomplete, needReload;
    int year, month, day, hour, minute, priority, sflag;   // value means that day   sflag : 1 come from schedule
    int ptmp, moreFuctPosiX, moreFuctPosiY;
    DatePicker fromDate, toDate;
    String absID;
    String [] backup;  // title context ps fromHour fromMinute toHour toMinute fromDate toDate
    Thread animThread;
    MsgBox checDeletekMsg, checkBackMsg, cancelMsg, completeMsg;
    Schedule lastPage;
    

    EventUI(){
        year = LocalDate.now().getYear();
        month = LocalDate.now().getMonthValue();
        day = LocalDate.now().getDayOfMonth();
        if( Calendar.getInstance().get(Calendar.MINUTE) > 30 ){
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+1;
            minute = 0;
        }else{
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            minute = 30;
        }
        init();
        createMFThread();
    }
    EventUI(EventCenter ECc){
        year = LocalDate.now().getYear();
        month = LocalDate.now().getMonthValue();
        day = LocalDate.now().getDayOfMonth();
        if( Calendar.getInstance().get(Calendar.MINUTE) > 30 ){
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+1;
            minute = 0;
        }else{
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            minute = 30;
        }
        EC = ECc;
        init();
        createMFThread();
    }

    EventUI(int y, int m, int d){
        year = y;
        month = m;
        day = d;
        if( Calendar.getInstance().get(Calendar.MINUTE) > 30 ){
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+1;
            minute = 0;
        }else{
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            minute = 30;
        }

        init();
        createMFThread();
    }
    EventUI(int y, int m, int d, int h, int minutee){
        year = y;
        month = m;
        day = d;
        hour = h;
        minute = minutee;
        init();
        createMFThread();
    }
    
    void init(){       //////////// 週期事件? ??? 用另外一種方式維護? 獨立一個檔?   // 錯誤提示
        Error = false;
        flagg = false;
        isAllDay = false;
        edited = false;
        editable = true;
        moreFuctFlag = false;
        MTFlag = false;
        soundFlag = false;
        ifEnterSchedule = true;
        iscomplete = false;
        needReload = false;
        moreFuctPosiX = 1300; 
        moreFuctPosiY = 10;
        priority = 0;
        absID = "";
        backup = new String[10];
        bg = new MyImage("bg_text04.png");
        fromTo = new MyImage("bg_text05.png");
        bgComma = new MyImage("bg_text07.png", new Point(733, 118));
        bg2 = new MyImage("bg_sora01.jpg", 1280);
        errorImage = new MyImage("ERROR.png", new Point(604, 90));
        AllDay = new MyButton("", "allDay.png", 99, new Point(300, 155));
        checDeletekMsg = new MsgBox("刪除後無法復原！\n真的要刪除嗎？", 1);
        checkBackMsg = new MsgBox("返回會喪失所有未儲存的內容！\n真的要返回嗎？", 1);
        cancelMsg = new MsgBox("取消會喪失所有未儲存的內容！\n真的要取消更變嗎？", 1);
        completeMsg = new MsgBox("這件事完成了嗎？", 1);


        
        AllDay.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(editable)
                if(!isAllDay){
                    setIsAllDay();
                }else{
                    setNotAllDay(true);
                }
            }
        });
        AllDay.btn.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (!isAllDay) AllDay.setImage(AllDay.enter);
            }
        });
        AllDay.btn.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (!isAllDay) AllDay.setImage(AllDay.normal);
            }
        });
        AllDay.btn.setStyle("-fx-background-color: transparent;");

        completeBtn = new MyButton("", "complete.png", 150, 340, new Point(20,40));

        completeBtn.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(!iscomplete){
                    completeMsg.setMsg("這件事完成了嗎？");
                }else{
                    completeMsg.setMsg("其實這件事還沒完成？");
                }
                completeMsg.show(pane);
            }
        });
        completeBtn.btn.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (!iscomplete) completeBtn.setImage(completeBtn.enter);
            }
        });
        completeBtn.btn.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (!iscomplete) completeBtn.setImage(completeBtn.normal);
            }
        });
        completeBtn.btn.setStyle("-fx-background-color: transparent;");

        completeMsg.yes.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY) {
                
                setIsComplete(true);
                completeMsg.hide();
                try {
                    SaveEdit();
                    // if(DateFac.dateToInt(fromDate.getValue()) >= DateFac.dateToInt(LocalDate.now()) &&
                    //      DateFac.dateToInt(toDate.getValue()) <= DateFac.dateToInt(LocalDate.now()))
                        needReload = true;
                } catch (IOException e1) {
                    System.out.println("儲存完成資訊失敗");
                }

            }
        });


        Add = new MyButton("", "add_btn.png", 108, new Point(446, 580));
        Add.btn.setStyle("-fx-background-color: transparent;");
        Add.setButtonType(1);
        Add.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    absID = generAbsID();
                    addNewEvent();
                    year = fromDate.getValue().getYear();
                    month = fromDate.getValue().getMonthValue();
                    day = fromDate.getValue().getDayOfMonth();
                    leave();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        editbtn = new MyButton("", "edit_btn.png", 100, 45,  new Point(1080, 90));
        editbtn.btn.setStyle("-fx-background-color: transparent;");
        editbtn.setButtonType(1);
        editbtn.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                edit();
            }
        });

        checkBackMsg.yes.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY) {
                checkBackMsg.hide();
                leave();
            }
        });

        backbtn = new MyButton("", "back2_btn.png", 100, 45, new Point(1150, 20));
        backbtn.btn.setStyle("-fx-background-color: transparent;");
        backbtn.setButtonType(1);
        backbtn.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {  // 如果正在編輯 則跳出提示框
                if(checkIfdifferent()) checkBackMsg.show(pane);
                else justleave();
            }
        });
        morebtn = new MyButton("", "dot_btn.png", 100, 45, new Point(1150, 160));
        morebtn.btn.setStyle("-fx-background-color: transparent;");
        morebtn.setButtonType(1);
        setMoreFuctBtnAction(morebtn);


        Save = new MyButton("", "save_btn.png", 108, new Point(250, 580));
        Save.btn.setStyle("-fx-background-color: transparent;");
        Save.setButtonType(1);
        Save.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    SaveEdit();
                } catch (IOException e1) {
                    System.out.println("修改儲存失敗");
                }
            }
        });

        cancelMsg.yes.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY) {
                cancelMsg.hide();
                cancelEdit();
            }
        });

        Cancel = new MyButton("", "cancel_btn.png", 108, new Point(550, 580));
        Cancel.btn.setStyle("-fx-background-color: transparent;");
        Cancel.setButtonType(1);
        Cancel.btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(checkIfdifferent()) cancelMsg.show(pane);
                else cancelEdit();
            }
        });


        pane = new Pane();
        bg2.show(pane);
        bg.show(pane);
        fromTo.show(pane);
        

        
        fromDate = new DatePicker();
        toDate = new DatePicker();
        setDPStyle(fromDate);
        fromDate.setLayoutX(620);
        fromDate.setLayoutY(113.5);
        fromDate.valueProperty().addListener((ov, oldValue, newValue) -> {
                toDate.setValue(newValue.plusDays(0));
        });
        

        setDPStyle(toDate);
        if(hour == 23 && minute >= 30) 
            toDate.setValue(LocalDate.of(year, month, day).plusDays(1) );
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>(){
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(fromDate.getValue().plusDays(0))) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
        }};
        toDate.setDayCellFactory(dayCellFactory);
        toDate.setLayoutX(620);
        toDate.setLayoutY(155.5);




        pane.getChildren().add(fromDate);
        pane.getChildren().add(toDate);

        checDeletekMsg.yes.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY) {
                deleteCurrentEvent();
                checDeletekMsg.hide();
                leave();
            }
        });


        moreFuct = new ArrayList<MyButton>();
        moreFuct.add(new MyButton("", "delete_btn.png", 100, 45, new Point(1400, 1400)));
        moreFuct.get(0).btn.setStyle("-fx-background-color: transparent;");
        moreFuct.get(0).setButtonType(1);
        moreFuct.get(0).btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {  // 跳出提示框
                checDeletekMsg.show(pane);

            }
        });
        setMoreFuctBtnAction(moreFuct.get(0));

        moreFuct.add(new MyButton("", "todo_btn.png", 100,45, new Point(1400, 1400)));
        moreFuct.get(1).btn.setStyle("-fx-background-color: transparent;");
        moreFuct.get(1).setButtonType(1);
        moreFuct.get(1).btn.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {  
                System.out.println(checkIfdifferent());
            }
        });
        setMoreFuctBtnAction(moreFuct.get(1));
        

        
        // deletebtn.show(moreFuct);
        // copybtn.show(moreFuct);

        // moreFuct.setSpacing(-15);
        // moreFuct.setPadding(new Insets(-5,-10,-5,-10));
        // moreFuct.setLayoutX(moreFuctPosi);
        // moreFuct.setLayoutY(285);
        

        setTextAddr();


        
        //  title.getContextMenu().setStyle("-fx-font-size: 20px;");

        pane.getChildren().add(title);
        pane.getChildren().add(context);
        pane.getChildren().add(ps);
        pane.getChildren().add(fromHour);
        pane.getChildren().add(fromMinute);
        pane.getChildren().add(toHour);
        pane.getChildren().add(toMinute);




        editbtn.show(pane);
        backbtn.show(pane);
        morebtn.show(pane);
        for(int i = 0; i < moreFuct.size(); ++i){
            moreFuct.get(i).show(pane);
        }


        checkError();


        bgComma.show(pane);
        animThreadFlag = true;
        

    }



    void edit(){
        editable = true;
        title.setEditable(true);
        context.setEditable(true);
        ps.setEditable(true);
        fromHour.setEditable(true);
        fromMinute.setEditable(true);
        toHour.setEditable(true);
        toMinute.setEditable(true);
        fromDate.setDisable(false);
        toDate.setDisable(false);
        backup();
        editbtn.hide(pane);
        morebtn.hide(pane);
        completeBtn.hide(pane);
        Save.show(pane);
        Cancel.show(pane);
        if(!isAllDay) AllDay.show(pane);
    }

    void backup(){
        backup[0] = title.getText();
        backup[1] = context.getText();
        backup[2] = ps.getText();
        backup[3] = fromHour.getText();
        backup[4] = fromMinute.getText();
        backup[5] = toHour.getText();
        backup[6] = toMinute.getText();
        backup[7] = DateFac.dateToInt(fromDate.getValue() ) + "";
        backup[8] = DateFac.dateToInt(toDate.getValue() ) + "";
        backup[9] = isAllDay+"";
    }

    void SaveEdit() throws IOException{ 
        needReload = true;
        backup();
        int oriFD = Integer.parseInt(backup[7]), oriTD = Integer.parseInt(backup[8]);
        Delete(oriFD, oriTD);
        addNewEvent();  
        cancelEdit();
    }

    void Delete(int fd, int td) throws IOException{
        int lastMonth = -1;
        EventCenter df = new EventCenter(); 
        for(int i = fd; i <= td; ++i){
            int thisMonth = DateFac.intToMonth(i), d = DateFac.intToDayOfMonth(i);
            if(lastMonth != thisMonth){
                if(lastMonth != -1)
                    df.saveToFile();
                df = new EventCenter( DateFac.intToYMString(i));  
                lastMonth = thisMonth;
            }
            df.DeleteEvent(d, absID);
        }
        df.saveToFile();
    }
    
    void setIsComplete(boolean willSet){
        if (!iscomplete ) {
            if(willSet) {
                iscomplete = true;
                completeBtn.mImage.setImage("complete_clicked.png");
            }else{
                completeBtn.mImage.setImage("complete.png");
            }
        }else{
            if(willSet) {
                iscomplete = false;
                completeBtn.mImage.setImage("complete.png");
            }else{
                completeBtn.mImage.setImage("complete_clicked.png");
            }
        }
    }

    void setIsAllDay(){
        AllDay.mImage.setImage("allDay_clicked.png");
        isAllDay = true;
        checkError();
        bgComma.hide(pane);
        pane.getChildren().remove(fromHour);
        pane.getChildren().remove(toHour);
        pane.getChildren().remove(fromMinute);
        pane.getChildren().remove(toMinute);
    }
    void setNotAllDay(boolean showItem){
        AllDay.mImage.setImage("allDay.png");
        if(showItem){
            bgComma.show(pane);
            pane.getChildren().add(fromHour);
            pane.getChildren().add(toHour);
            pane.getChildren().add(fromMinute);
            pane.getChildren().add(toMinute);
        }
        isAllDay = false;
        checkError();
    }

    boolean checkIfdifferent(){
        String str1 = title.getText()+context.getText()+ps.getText(), str2 = "";
        if(str1.length() == 0 ) return false;
        str1 += fromHour.getText()+fromMinute.getText()+toHour.getText()+toMinute.getText()+
                        DateFac.dateToInt(fromDate.getValue() )+DateFac.dateToInt(toDate.getValue() ) + isAllDay;
        for(int i = 0; i < backup.length; ++i)
            str2 += backup[i];
        return !str1.equals(str2);
    }

    void cancelEdit(){
        
        Save.hide(pane);
        Cancel.hide(pane);
        editbtn.show(pane);
        morebtn.show(pane);
        completeBtn.show(pane);
        title.setText(backup[0]);
        context.setText(backup[1]);
        ps.setText(backup[2]);
        fromHour.setText(backup[3]);
        fromMinute.setText(backup[4]);
        toHour.setText(backup[5]);
        toMinute.setText(backup[6]);
        fromDate.setValue(DateFac.intToDate(backup[7]));
        toDate.setValue(DateFac.intToDate(backup[8]));

        if(isAllDay){
            setNotAllDay(true);
        }

        isAllDay = Boolean.parseBoolean(backup[9]);
        editable = false;
        title.setEditable(false);
        context.setEditable(false);
        ps.setEditable(false);
        fromHour.setEditable(false);
        fromMinute.setEditable(false);
        toHour.setEditable(false);
        toMinute.setEditable(false);
        fromDate.setDisable(true);
        toDate.setDisable(true);
        if(!isAllDay){
            AllDay.hide(pane);
            setNotAllDay(false);
        }
    }

    void setMode(int mode){
        if(mode == 1){
            Add.show(pane);
            editable = true;
            editbtn.hide(pane);
            morebtn.hide(pane);
            AllDay.show(pane);
        }
    }
    void setDate(int yy, int mm, int dd){
        year = yy;
        month = mm;
        day = dd;
    }


    void deleteCurrentEvent(){
        try {
            Delete(DateFac.dateToInt(fromDate.getValue()),  DateFac.dateToInt(toDate.getValue()));
        } catch (IOException e) {
            System.out.println("刪除失敗");
        }
    }

    void addNewEvent() throws IOException{
        int fd = DateFac.dateToInt(fromDate.getValue()), td = DateFac.dateToInt(toDate.getValue());
        if(timeToInt(toHour, toMinute) == 0) --td;
        int lastMonth = -1;
        EventCenter df = new EventCenter();
        for(int i = fd;i <= td; ++i){
            int thisMonth = DateFac.intToMonth(i), d = DateFac.intToDayOfMonth(i);
            int s = i == fd ? timeToInt(fromHour, fromMinute) : 0, e = i == td ? timeToInt(toHour, toMinute) : 1440;
            if(e == 0) e = 1440;
            if(lastMonth != thisMonth){
                if(lastMonth != -1) df.saveToFile();
                df = new EventCenter( DateFac.intToYMString(i));
                lastMonth = thisMonth;
            }
            if(priority != 0) df.addNewEvent(ConventJson(d, priority, s, e));
            else df.addNewEvent(ConventJson(d, df.getDayData(d).length(), s, e));
        }
        df.saveToFile();
    }

    JSONObject ConventJson(int day, int prior, int s, int e){ 
        JSONObject ret = new JSONObject();
        ret.put("Title", title.getText());
        ret.put("Context", context.getText());
        ret.put("PS", ps.getText());
        ret.put("Completion", iscomplete);
        ret.put("Priority", prior);
        ret.put("Allday", isAllDay? 1: 0);
        ret.put("StartTime", s);
        ret.put("EndTime", e);
        ret.put("Day", day);
        ret.put("AbsID", absID);
        ret.put("FromDate", DateFac.dateToInt( fromDate.getValue()) );
        ret.put("ToDate", DateFac.dateToInt( toDate.getValue()) );
        return ret;
    }

    int timeToInt(TextField h, TextField m){
        return Integer.parseInt(h.getText())*60 + Integer.parseInt(m.getText());
    }

    int MonthDays(int y, int m){
        if(m == 2){
            if(y % 4 != 0) return 28;
            else if(y % 100 != 0) return 29;
            else if( y % 400 != 0) return 28;
            return 29;
        }
        int [] days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        return days[m];
    }

    void setEventCenter(EventCenter ECc){
        EC = ECc;
    }
    void load( int day, int EventId ){
        init();
        JSONObject event = EC.getDayData(day).getJSONObject(EventId);
        editable = false;
        title.setEditable(false);
        context.setEditable(false);
        ps.setEditable(false);
        fromHour.setEditable(false);
        fromMinute.setEditable(false);
        toHour.setEditable(false);
        toMinute.setEditable(false);
        fromDate.setDisable(true);
        toDate.setDisable(true);

        title.setText(event.getString("Title"));
        context.setText(event.getString("Context"));
        ps.setText(event.getString("PS"));
        int s = event.getInt("StartTime"), e = event.getInt("EndTime");
        fromHour.setText((int)(s/60)+"");
        fromMinute.setText((int)(s%60)+"");
        toHour.setText((int)(e/60)+"");
        toMinute.setText((int)(e%60)+"");
        fromDate.setValue(DateFac.intToDate(event.getInt("FromDate")));
        toDate.setValue(DateFac.intToDate(event.getInt("ToDate")));
        isAllDay = event.getInt("Allday") == 1;
        absID = event.getString("AbsID");
        priority = event.getInt("Priority");
        iscomplete = event.getBoolean("Completion");
        checkZeroPadding();
        if(isAllDay){
            AllDay.show(pane);
            setIsAllDay();
        }
        setIsComplete(false);

        completeBtn.show(pane);
        backup();        

    }



    void setMoreFuctBtnAction(MyButton mbtn){
        mbtn.btn.setOnMouseEntered(e->{
            if(!moreFuctFlag){
                moreFuctMoveEnd();
                moreFuctFlag = true;
                moreFuctMoveStart();
            }
        });
        mbtn.btn.setOnMouseExited(e->{
            if(moreFuctFlag){
                moreFuctMoveEnd();
                moreFuctFlag = false;
                moreFuctMoveStart();
            }
        });
    }

    void createMFThread(){
        animThread = new Thread(){
            @Override
            public void run(){
                System.out.println(this.getId());
                while(animThreadFlag){
                    if(MTFlag) {
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run(){
                                if(moreFuctFlag){
                                    if(moreFuctPosiX > 1080){
                                        moreFuctPosiX -= 10;
                                        moreFuctPosiY += 10;
                                        setMoreFuctPosi(moreFuctPosiX, moreFuctPosiY);
                                        if(moreFuctPosiX < 1200){
                                            morebtn.setXY(moreFuctPosiX-70, moreFuctPosiY+70);
                                        }
                                        if(!soundFlag){
                                            MoreFuctSound(false);
                                            soundFlag = true;
                                        }
                                    }else{
                                        MTFlag = false;
                                        soundFlag = false;
                                        MoreFuctSound(true);

                                    }
                                }else{
                                    if(moreFuctPosiX < 1300){
                                        moreFuctPosiX += 10;
                                        moreFuctPosiY -= 10;
                                        setMoreFuctPosi(moreFuctPosiX, moreFuctPosiY);
                                        if(moreFuctPosiX < 1225){
                                            morebtn.setXY(moreFuctPosiX-70, moreFuctPosiY+70);
                                        }
                                        if(!soundFlag){
                                            MoreFuctSound(false);
                                            soundFlag = true;
                                        }
                                    }else{
                                        MTFlag = false;
                                        MoreFuctSound(true);
                                        soundFlag = false;
                                        
                                    }
                                }
                            }
                        });
                    }
                    try{
                        sleep(16);
                    }catch(Exception e){System.out.println("term");}
                }

            }
        };
        animThread.start();
    }

    void setMoreFuctPosi(double x, double y){  // left down
        for(int i = 0; i < moreFuct.size(); ++i){
            moreFuct.get(i).setXY(x + i * 70, y - i*70);
        }
    }
    void MoreFuctSound(boolean haveSound){
        if(haveSound){
            for(int i = 0; i < moreFuct.size(); ++i){
                moreFuct.get(i).setEnterSoundVolume(-1);
            }
        }else{
            for(int i = 0; i < moreFuct.size(); ++i){
                moreFuct.get(i).setEnterSoundVolume(0);
            }
        }
    }

    void terminalThread(){
        animThreadFlag = false;
        animThread.interrupt();
    }
    void moreFuctMoveStart(){
        MTFlag = true;
    }

    void moreFuctMoveEnd(){
        MTFlag = false;

    }
    void show(Pane root){
        root.getChildren().add(pane);
        depend = root;
    } 
    void show(Pane root, int flag){
        sflag = flag;
        root.getChildren().add(pane);
        depend = root;
    } 
    void show(Pane root, Schedule s){
        lastPage = s;
        root.getChildren().add(pane);
        depend = root;
    }

    void justleave(){
        if(needReload)
            if(sflag == 0) Main.reloadSchedule(year, month, day, false);
            else if(sflag == 1) Main.reloadSchedule(year, month, day, Integer.parseInt(fromHour.getText()), Integer.parseInt(fromMinute.getText()) );
        depend.getChildren().remove(pane);
    }
    void leave(){
        Main.reloadSchedule(year, month, day, Integer.parseInt(fromHour.getText()), Integer.parseInt(fromMinute.getText()) );
        depend.getChildren().remove(pane);
    }
    void setIfEnterSchedule(boolean ifEnter){
        ifEnterSchedule = ifEnter;
    }

    String generAbsID(){
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    void setFont(Font font){
        title.setFont(font); 
        fromHour.setFont(font); 
        fromMinute.setFont(font); 
        toHour.setFont(font); 
        toMinute.setFont(font); 
        context.setFont(font); 
        ps.setFont(font); 
    }

    void checkZeroPadding(){
        String tmp = fromHour.getText();
        if(tmp.length() == 1) fromHour.setText("0" + tmp);
        if( (tmp = toHour.getText() ).length() == 1 ) toHour.setText("0"+tmp);
        if( (tmp = fromMinute.getText() ).length() == 1 ) fromMinute.setText("0"+tmp);
        if( (tmp = toMinute.getText() ).length() == 1 ) toMinute.setText("0"+tmp);
    }

    void setDPStyle(DatePicker DP){
        if(hour == 24) 
            DP.setValue(LocalDate.of(year, month, day+1) );
        else DP.setValue(LocalDate.of(year, month, day) );
        DP.getStyleClass().add("DatePickerType1");
        DP.setStyle("-fx-font-size: 15px;-fx-background-color: transparent;");
        DP.getEditor().setDisable(true);
        DP.getEditor().setOpacity(1);
        DP.getEditor().setStyle("-fx-font-size: 20px;");
        DP.setMaxWidth(50);
        DP.setNodeOrientation(NodeOrientation.INHERIT);
        DP.setMaxWidth(150);

        DP.setOnScroll(e->{
            if(editable)
            if(e.getDeltaY() > 0)
                DP.setValue( DP.getValue().plusDays(1) );
            else
                DP.setValue( DP.getValue().plusDays(-1) );
            
        });
        DP.valueProperty().addListener((ov, oldValue, newValue) -> {
            checkError();
        } );
    }

    
    

    void setNotFocusOn(TextField tf){
        tf.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue){
                if(!newPropertyValue) {
                    if(tf.getText().length() == 1) 
                        tf.setText("0"+tf.getText());
                    else if( tf.getText().length() == 0 )
                        tf.setText("00");
                        checkError();
                }
            }
        });

    }

    void setLineLimit(TextArea TA, int line){
        TA.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isContentChange()) {
                if (change.getControlNewText().chars().filter(a -> a == '\n').count() < line) {
                    change.setText("");
                }
            }
            
            return change;
        }));
    }



    void scrollTime( TextField tf, int limit ){
        tf.setOnScroll(e->{
            if(editable){
                int n = Integer.parseInt(tf.getText()) + ( e.getDeltaY() > 0 ? 1 : -1 );
                if(n >= limit) n = 0;
                else if(n < 0) n = limit-1;
                tf.setText(n+"");
                checkError();
            }
        });
        tf.setOnMouseClicked(e->{
            if(editable)
                tf.selectAll();
        });
    }

    void checkError(){
        Boolean tmp = checkTime();
        if(Error != tmp) {
            Error = tmp;
            ErrorImage();
        }
    }

    Boolean checkTime(){
        try{
            if( fromDate.getValue().isAfter(toDate.getValue()) ) return true;
            int from = Integer.parseInt(fromHour.getText())*60 +Integer.parseInt(fromMinute.getText());
            int to = Integer.parseInt(toHour.getText())*60 +Integer.parseInt(toMinute.getText());
            if( ( to > from  || isAllDay)  || fromDate.getValue().isBefore(toDate.getValue() )  ) return false;
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    void ErrorImage(){
        if(Error) errorImage.show(pane);
        else errorImage.hide(pane);
    }


    void setTextAddr(){
        title = new TextField("");
        title.setLayoutY(113.5);
        title.setLayoutX(157);
        title.setMinSize(250, 35);
        title.setPromptText("T i t l e  |");
        title.setEditable(true);
        title.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        
        context = new TextArea();
        context.setLayoutY(197.5);
        context.setLayoutX(157);
        context.setMaxSize(680, 290);
        context.setPrefSize(680, 290);
        context.setPromptText("C o n t e x t |");
        context.setEditable(true);
        context.setWrapText(true);
        context.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        context.focusedProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                context.setScrollTop(Double.MIN_VALUE); // this will scroll to the top
            }
        });
        context.setTextFormatter(new TextFormatter<>(change -> {  //change.getControlNewText().chars().filter(a -> a == '\n').count() < 3
            if (change.isContentChange()) {
                String tmpp = change.getControlNewText();
                int leng = tmpp.length();
                if (leng < 3 || tmpp.charAt(leng-1) + tmpp.charAt(leng-2) + tmpp.charAt(leng-3) != 30 ) {
                    if(!flagg){
                        flagg = true;
                        context.setText(context.getText()+"\n\r\n\r\n\r");
                    }
                }
            }
            flagg = false;
            return change;
        }));

        ps = new TextArea();
        ps.setLayoutY(490);
        ps.setLayoutX(157);
        ps.setMaxSize(680, 73);
        ps.setPrefSize(680, 73);
        ps.setPromptText("Something to add ? ");
        ps.setEditable(true);
        ps.setWrapText(true);
        ps.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        ps.focusedProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                ps.setScrollTop(Double.MIN_VALUE); // this will scroll to the top
            }
        });
        ps.setTextFormatter(new TextFormatter<>(change -> {  //change.getControlNewText().chars().filter(a -> a == '\n').count() < 3
            if (change.isContentChange()) {
                String tmpp = change.getControlNewText();
                int leng = tmpp.length();
                if (leng < 1 || tmpp.charAt(leng-1)!= 10 ) {
                    if(!flagg){
                        flagg = true;
                        ps.setText(ps.getText()+"\n\r");
                    }
                }
            }
            flagg = false;
            return change;
        }));


        fromHour = new TextField( hour>23 ? "00" : hour < 10 ? "0"+hour : hour+"");
        fromHour.setMaxSize(50, 35);
        fromHour.setLayoutY(113.5);
        fromHour.setLayoutX(750);
        fromHour.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        scrollTime(fromHour, 24);
        setNotFocusOn(fromHour);

        fromHour.textProperty().addListener((ChangeListener<? super String>) new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if(!newValue.matches("\\d?\\d?") || newValue.length() > 2){
                    fromHour.setText(oldValue);
                }else if( newValue.length() > 1 &&  (newValue.charAt(0)-48)*10 + (newValue.charAt(1)-48) > 23 ){
                    fromHour.setText("23");
                    fromMinute.setFocusTraversable(false);
                    fromMinute.requestFocus();
                }
                if( oldValue.length() == 1  && newValue.length() == 2 && newValue.charAt(0) == oldValue.charAt(0) ) {
                    fromMinute.setFocusTraversable(false);
                    fromMinute.requestFocus();
                }
            }
        });

        fromMinute = new TextField( minute < 10 ? "0"+ minute : (minute +  "") );
        fromMinute.setMaxSize(50, 35);
        fromMinute.setLayoutY(113.5);
        fromMinute.setLayoutX(800);
        fromMinute.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        scrollTime(fromMinute, 60);
        setNotFocusOn(fromMinute);

        fromMinute.textProperty().addListener((ChangeListener<? super String>) new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if(!newValue.matches("\\d?\\d?") || newValue.length() > 2){
                    fromMinute.setText(oldValue);
                }else if( newValue.length() > 1 &&  (newValue.charAt(0)-48)*10 + (newValue.charAt(1)-48) > 59 ){
                    fromMinute.setText("59");
                    toHour.setFocusTraversable(false);
                    toHour.requestFocus();
                }
                if( oldValue.length() == 1  && newValue.length() == 2 && newValue.charAt(0) == oldValue.charAt(0) ) {
                    toHour.setFocusTraversable(false);
                    toHour.requestFocus();
                }
            }
        });

        int tmp = (minute >= 30 ? hour+1 : hour);
        toHour = new TextField( tmp>23 ? "00" : tmp < 10 ? "0"+tmp : tmp+"");
        toHour.setMaxSize(50, 35);
        toHour.setLayoutY(155.5);
        toHour.setLayoutX(750);
        toHour.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        scrollTime(toHour, 24);
        setNotFocusOn(toHour);

        toHour.textProperty().addListener((ChangeListener<? super String>) new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if(!newValue.matches("\\d?\\d?") || newValue.length() > 2){
                    toHour.setText(oldValue);
                }else if( newValue.length() > 1 &&  (newValue.charAt(0)-48)*10 + (newValue.charAt(1)-48) > 23 ){
                    toHour.setText("23");
                    toMinute.setFocusTraversable(false);
                    toMinute.requestFocus();
                }
                if( oldValue.length() == 1  && newValue.length() == 2 && newValue.charAt(0) == oldValue.charAt(0) ) {
                    toMinute.setFocusTraversable(false);
                    toMinute.requestFocus();
                }
            }
        });

        int ttmp = (minute <30 ? minute + 30 : (minute + 30)%60 );
        toMinute = new TextField( ttmp < 10 ? "0"+ttmp : ttmp+""  );
        toMinute.setMaxSize(50, 35);
        toMinute.setLayoutY(155.5);
        toMinute.setLayoutX(800);
        toMinute.setStyle("-fx-text-box-border: transparent;-fx-faint-focus-color: transparent;-fx-background-color: transparent, transparent;-fx-text-fill:black;-fx-font-size: 20px;");
        scrollTime(toMinute, 60);
        setNotFocusOn(toMinute);

        toMinute.textProperty().addListener((ChangeListener<? super String>) new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
                if(!newValue.matches("\\d?\\d?") || newValue.length() > 2){
                    toMinute.setText(oldValue);
                }else if( newValue.length() > 1 &&  (newValue.charAt(0)-48)*10 + (newValue.charAt(1)-48) > 59 ){
                    toMinute.setText("59");
                    title.setFocusTraversable(false);
                    title.requestFocus();
                }
                if( oldValue.length() == 1  && newValue.length() == 2 && newValue.charAt(0) == oldValue.charAt(0) ) {
                    title.setFocusTraversable(false);
                    title.requestFocus();
                }
            }
        });
        

    }
}

class controlFlag{
    /* 
    0 : nothing
    1 : delete event check

    */
    int flag; 
    controlFlag(){flag = 0;}
    controlFlag(int flagg){flag = flagg;}

    void setFlag(int flagg){flag = flagg;}
    int getFlag(){return flag;}
}