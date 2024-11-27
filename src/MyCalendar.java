import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.MouseInfo;

public class MyCalendar {
    Pane pane, depend;
    Calendar current, showing;
    Text T_Month, T_year; // 
    MyButton last, next;
    ArrayList<DayBlock> dayblocks;
    ArrayList<TextCanvas> todayEvent;
    Point posi;
    Font font;
    EventCenter EC;
    Schedule schedule;
    int showingYear, showingMonth;
    MyImage back, calendarr, bar1, evlist;
    MyImage [] bar23 ; 
    boolean flag, isPassByBtn, Reloading;
    EventUI eui;
    MyCalendar(Font fontt) throws IOException{
        font = fontt;
        current = Calendar.getInstance();

        init();
        setCalenderData(current.get(Calendar.YEAR), current.get(Calendar.MONTH));
      //  setCalenderData(2022, 5);
    }

    void init(){
        Reloading = false;
        pane = new BorderPane();

        pane.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.SECONDARY && !schedule.RbtnFlag)
                leave();
            schedule.RbtnFlag = false;
        });

        long t1 = System.currentTimeMillis(), t2;

        back = new MyImage("bg_sora03.moe",1280);

        t2 = System.currentTimeMillis();
        System.out.println("花費時間" + (t2-t1) );
        evlist = new MyImage("eventlist5.png", new Point(815,90));
        calendarr = new MyImage("calendar.png");
        back.show(pane);
        calendarr.show(pane);
        evlist.show(pane);
        posi = new Point();
        T_Month = new Text();
        T_year = new Text();
        pane.getChildren().add(T_Month);
        pane.getChildren().add(T_year);
        schedule = new Schedule(this);

        last = new MyButton("", "arrow_btn.png", 240, new Point(129, 679));
        last.setButtonType(1);
        last.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY){
                isPassByBtn = true;
                if(--showingMonth < 0) showingMonth = 11;
                if(showingMonth == 11) --showingYear;
                try {
					setCalenderData(showingYear, showingMonth);
				} catch (IOException e1) {
					System.out.println("日曆讀取失敗");
				}
            }
        });
        last.show(pane);

        next = new MyButton("", "arrow_btn.png", 240, 180, new Point(693, 679));
        next.setButtonType(1);
        next.btn.setOnMouseClicked(e->{
            if(e.getButton() == MouseButton.PRIMARY){
                if(++showingMonth > 11) showingMonth = 0;
                if(showingMonth == 0) ++showingYear;
                try {
					setCalenderData(showingYear, showingMonth);
				} catch (IOException e1) {
					System.out.println("日曆讀取失敗");
				}
            }

        });
        next.show(pane);
        T_Month.setX(posi.x+70);
        T_Month.setY(posi.y+98);
        T_Month.setFont(font);
        T_Month.setStyle("-fx-font-size: 115px;");
        T_Month.setFill(Color.web("#3D1515"));
        T_year.setX(posi.x+700);
        T_year.setY(posi.y+98);
        T_year.setFont(font);
        T_year.setStyle("-fx-font-size: 50px;");
        T_year.setFill(Color.web("#3D1515"));

        bar1 = new MyImage("bar_ca.png");
        bar23 = new MyImage[2];
        bar23[0] = new MyImage("bar_ca.png");
        bar23[1] = new MyImage("bar_ca.png");
        eui = new EventUI();

    }

    JSONArray getTodayEvent(){
        current = Calendar.getInstance();
        String today = current.get(Calendar.YEAR) +"-"+ (current.get(Calendar.MONTH)+1);
        try {
            if(EC.date != today)
                return new EventCenter(today).getDayData(current.get(Calendar.DAY_OF_MONTH));
            else
                return EC.getDayData(current.get(Calendar.DAY_OF_MONTH));
        } catch (IOException e) {
            System.out.println("讀取今日事件失敗");
        }
        return null;
    }
    void displayTodayEvent(){
        JSONArray events = getTodayEvent();
        if(todayEvent != null){
            for(int i = 0; i < todayEvent.size(); ++i){
                todayEvent.get(i).hide(pane);
            }
            todayEvent.clear();
        }

        todayEvent = new ArrayList<TextCanvas>();
        ArrayList<nearCurrent> evlist = new ArrayList<nearCurrent>();
        for(int i = 0; i < events.length(); ++i){
            JSONObject ev = events.getJSONObject(i);
            if(ev.getInt("Allday") == 1 ){
                evlist.add( new nearCurrent(true, i, ev.getBoolean("Completion")) );
                continue;
            }
            evlist.add( new nearCurrent(ev.getInt("StartTime"), ev.getInt("EndTime"), i, Calendar.getInstance(),ev.getBoolean("Completion") ) );
        }
        Collections.sort(evlist);
        for(int i = 0; i < evlist.size(); ++i){
            JSONObject ev = events.getJSONObject( evlist.get(i).index );
            todayEvent.add(new TextCanvas(200, 50));
            if(evlist.get(i).iscomplete){
                todayEvent.get(i).drawBackground(1, new String[]{"#123456", "#223456"} );
                todayEvent.get(i).setText(ev.getString("Title")+ " (完 成) ");
            }else if(evlist.get(i).overtime){
                todayEvent.get(i).drawBackground(1, new String[]{"#CB4042", "#D0104C"} );
                todayEvent.get(i).setText(ev.getString("Title")+ " (超過時間！) ");
            }else if(evlist.get(i).isnow){
                todayEvent.get(i).drawBackground(1, new String[]{"#F19483", "#D7C4BB"} );
                todayEvent.get(i).setText(ev.getString("Title")+ " (現在該做的) ");
            }else if(evlist.get(i).allday){
                todayEvent.get(i).drawBackground(1, new String[]{"#F17C67", "#F19483"} );
                todayEvent.get(i).setText(ev.getString("Title")+ " (全天事件！) ");
            }else{
                todayEvent.get(i).drawBackground(1, new String[]{"#D75455", "#D7C4BB"} );
                todayEvent.get(i).setText(ev.getString("Title")+ " (晚點的事) ");
            }
            todayEvent.get(i).setXY(850, 180 + i * 50);
            todayEvent.get(i).show(pane);
            
        }
        setEventBtn(EC);

    }
    void setEventBtn(EventCenter EC){
        eui.setEventCenter(EC);
        for(int i = 0; i < todayEvent.size(); ++i){
            final int index = todayEvent.get(i).id;
            final int indexInArr = i;
            todayEvent.get(i).btn.setOnMouseClicked(e->{
                if(e.getButton() != MouseButton.SECONDARY){
                    try{
                        EC.load();
                    }catch(Exception eee) {System.out.println("載入讀取失敗");}
                    eui.setDate(current.get(Calendar.YEAR), current.get(Calendar.MONTH)+1, current.get(Calendar.DAY_OF_MONTH));
                    eui.load(current.get(Calendar.DAY_OF_MONTH), index);
                    eui.setIfEnterSchedule(false);

                    eui.show(pane);        

                }
                else if(e.getButton() == MouseButton.SECONDARY){
                    leave();
                }
            });
            todayEvent.get(i).btn.setOnMouseEntered(ee->todayEvent.get(indexInArr).startAnimation() );
            todayEvent.get(i).btn.setOnMouseExited(ee->todayEvent.get(indexInArr).stopAnimation() );
        }
    }

    void leave(){
        depend.getChildren().remove(pane);
    }

    void setToday(){
        current = Calendar.getInstance();
        try {
            setCalenderData(current.get(Calendar.YEAR), current.get(Calendar.MONTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setCalenderData(int year, int month) throws IOException{
        showingYear = year;
        showingMonth = month;
        if(dayblocks != null){
            for(int i = 0; i < dayblocks.size(); ++i)
            dayblocks.get(i).hide(pane);
        }
        writeDays(year, month);
        ECRead(year, month);
        
    }



    void ECRead(int year, int month) throws IOException{
        EC = new EventCenter(year + "-" + (month+1));
        for(int i = 0; i < dayblocks.size(); ++i){
            if(dayblocks.get(i).month != (month+1) )
                continue;
            EC.setTopEvent(dayblocks.get(i));
            for(int j = 0; j < 3; ++j)
                dayblocks.get(i).TopEvent[j].drawBackground(1, new String[]{"#111111", "#884216"});
            dayblocks.get(i).setButton();
        }
        displayTodayEvent();
    }

    void show(Pane root ){
        depend = root;
        root.getChildren().add(pane);

    }
    void reload(){
        Reloading = true;
        long t = System.currentTimeMillis();
        try {
            ECRead(showingYear, showingMonth);
        } catch (IOException e) {
            Reloading = false;
            e.printStackTrace();
        }
        Reloading = false;
        System.out.println("Reload, spend : " + (System.currentTimeMillis() - t));
        
    }


    void writeDays(int year, int month){
        dayblocks = new ArrayList<DayBlock>();
        showing = new GregorianCalendar(year, month, 1);
        Calendar lastMon = getLastMon(year, month);
        String strr = (showing.get(Calendar.MONTH)+1)+"";
        if(strr.length() < 2) strr = " " + strr;
        T_Month.setText( strr );
        T_year.setText(year+"");


        int maxDay = showing.getActualMaximum(Calendar.DATE), firstDay = showing.get(Calendar.DAY_OF_WEEK )-1, counter = 1 ; // sun = 0, sat = 6

        for(int i = 0; i < firstDay; ++i){
            lastMon.set(Calendar.DATE, lastMon.getActualMaximum(Calendar.DATE) - firstDay + i + 1 );
            dayblocks.add( new DayBlock(font, lastMon, 2, this) );
        }
        for(int i = 1; i <= maxDay; ++i){
            showing.set(Calendar.DATE, i);
            int week = (i - (7 - firstDay) +7 ) % 7;
            if ( week <= 1 )
                dayblocks.add( new DayBlock(font, showing, 1, this) );
            else 
                dayblocks.add( new DayBlock(font, showing, 0, this) );

            if( week == 0 ) counter += 1;
        }
        if(counter != 6){
            showing.set(Calendar.MONTH, showing.get(Calendar.MONTH)+1);
            for(int i = 1; i <= 7 - ( (maxDay -(7 - firstDay) )% 7  ); ++i  ){
                showing.set(Calendar.DATE, i);
                dayblocks.add( new DayBlock(font, showing, 2, this) );
            }
        }
        Point firstPosi = new Point(posi.x+20,posi.y+160 ) ;
        for(int i = 0; i < 5; ++i)
            for(int j = 0; j < 7; ++j){
                dayblocks.get(i*7+j).setXY(firstPosi.x + j * 114, firstPosi.y + i * 100);
                dayblocks.get(i*7+j).show(pane);
            }
        flag = false;
        for(int i = 35; i < dayblocks.size(); ++i){
            flag = true;
            dayblocks.get(i).setXY(firstPosi.x + (i-35)* 114, 656);
            dayblocks.get(i).show(pane);
            bar23[i-35].setXY(posi.x + 10 + (i-34)*114, 633);
            bar23[i-35].show(pane);
        }
        if(flag){
            bar1.setXY(posi.x + 10, 633);
            bar1.show(pane);
        }else{
            bar1.hide(pane);
            bar23[0].hide(pane);
            bar23[1].hide(pane);
        }
        double lastX = last.getX();
        last.setX(129+ (dayblocks.size()-35)* 114 + (10 * dayblocks.size()/36 ) );
        if(isPassByBtn){
            isPassByBtn = false;
            try {
				Robot robot = new Robot();
                robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + (int)(last.getX() - lastX)
                                , MouseInfo.getPointerInfo().getLocation().y);
			} catch (AWTException e) {
				System.out.println("自動位移滑鼠失敗");
			}

        }
   //     System.out.println("");
    }
    
    void showSchedule(int year, int month, int day, int hhh, int mmm){
        try {
            if(year != showingYear || month-1 != showingMonth)
			setCalenderData(year, month-1);
		} catch (IOException e) {
			System.out.println("讀取日曆失敗");
		}
        schedule.setDate(year + "-" + month + "-" + day);
        schedule.load(EC);
        schedule.show(pane, hhh, mmm);
    }

    void showSchedule(int year, int month, int day){
        try {
            if(year != showingYear || month-1 != showingMonth)
			setCalenderData(year, month-1);
		} catch (IOException e) {
			System.out.println("讀取日曆失敗");
		}
        schedule.setDate(year + "-" + month + "-" + day);
        schedule.load(EC);
        schedule.show(pane);
    }
    void showSchedule(String date){
        try {
            int tmp1 = Integer.parseInt(date.split("-")[0]), tmp2 = Integer.parseInt(date.split("-")[1])-1;
            if( tmp1 != showingYear || tmp2 != showingMonth)
			setCalenderData(tmp1, tmp2 );
		} catch (IOException e) {
			System.out.println("讀取日曆失敗");
		}
        schedule.setDate(date);
        schedule.load(EC);
        schedule.show(pane);
    }



    Calendar getLastMon(int year, int month){
        if(month == 1){
            --year;
            month = 12;
        }else month--;
        return new GregorianCalendar(year, month-1, 1);
    }

    Calendar getNextMon(int year, int month){
        if(month == 12){
            ++year;
            month = 1;
        }else month--;
        return new GregorianCalendar(year, month-1, 1);
    }

}

class nearCurrent implements Comparable<nearCurrent>{
    int s , e, index;
    boolean allday, overtime, iscomplete, isnow;
    nearCurrent(int ss, int ee, int in, Calendar current, boolean complete){ 
        s = ss;
        e = ee;
        index = in;
        allday = false;
        iscomplete = complete;
        int now = current.get(Calendar.HOUR_OF_DAY)*60 + current.get(Calendar.MINUTE);
        if(now > e){
            overtime = true;
        }
        if(now >= s && now <= e){
            isnow = true;
        }

    }
    nearCurrent(boolean isAllday, int in, boolean complete){
        allday = isAllday;
        index = in;
        iscomplete = complete;
        if(allday){
            s = 0;
            e = 1440;
        }
    }
    @Override
    public int compareTo(nearCurrent o){  // 時間開始早、延續長的擺第一
        if(s > o.s) return 1;
        if(s < o.s) return -1;
        if(e > o.e) return -1;
        if(e < o.e) return 1;
        return 0;
    }
}