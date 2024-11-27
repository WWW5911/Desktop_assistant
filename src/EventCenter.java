import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.print.Collation;

public class EventCenter {
    
    String filePath, date;
    JSONObject jsonObj;
    EventCenter(){}
    EventCenter(String date) throws IOException{ // System.getProperty("user.dir")
        this.date = date;
        date = EnDecoder.Encode(date);
        filePath = System.getProperty("user.dir") + "\\data\\event\\" + date + ".json";   // when first use?
        load();
    }

    void load() throws IOException{
        String str = ReadFile(filePath);
        try{
            jsonObj = new JSONObject(str);
        }catch(Exception e){
            createEmpty(filePath);
            str = ReadFile(filePath);
            jsonObj = new JSONObject(str);
        }
    }

    private String ReadFile(String path) throws IOException{
        FileReader fr;
        try{
            fr = new FileReader(new File(path));
        }catch(Exception e){
            createEmpty(path);
            fr = new FileReader(new File(path));
        }
        BufferedReader bf = new BufferedReader(fr);
        String context = "", strr = "";
        while( (strr = bf.readLine()) != null )
            context += strr;
        bf.close();
        return context;
    }

    public void assignEvent(ArrayList<DayBlock> al){   // everything should be sorted while it was saved
        for(int i = 0; i < al.size(); ++i){
            setTopEvent(al.get(i));
        }
    }
    public void setTopEvent(DayBlock db){
        int day = db.day ;
        try{
            if(jsonObj.getJSONArray("Day").length() >= day){
                JSONArray events = getDayData(day);
                for(int j = 0; j < 3; ++j){
                    if(j < events.length())
                        db.TopEvent[j].setText(events.getJSONObject(j).getString("Title"));
                    else
                        db.TopEvent[j].setText("");
                }
            }else 
                for(int j = 0; j < 3; ++j) db.TopEvent[j].setText("");
        }catch(Exception e){
            System.out.println("檔案損毀，建立新檔案");
            File bk = new File(System.getProperty("user.dir") + "\\data\\event\\brokenFile\\" + date + ".json" );
            if(!bk.exists()){
                bk.getParentFile().mkdirs();
                try {
                    bk.createNewFile();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }else{
                int count = 1;
                while(bk.exists())
                    bk = new File(System.getProperty("user.dir") + "\\data\\event\\brokenFile\\" + date + "_" + (count++) + ".json" );
            }
            FileWriter fw;
            try {
                fw = new FileWriter(bk);
                fw.write(jsonObj.toString());
                createEmpty(filePath);
                String str = ReadFile(filePath);
                jsonObj = new JSONObject(str);
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            

        }
    }


    public JSONArray getDayData(int day){
        day = day-1;
        JSONArray arr = jsonObj.getJSONArray("Day");
        if( arr.length() <= day){
            while(arr.length() <= day+1){
                JSONObject json = new JSONObject();
                json.put("Records", new JSONArray() );
                arr.put(json);
            }
            try {
                FileWriter fw = new FileWriter(filePath);
                fw.write(jsonObj.toString());
                fw.close();
                jsonObj = new JSONObject(ReadFile(filePath));
                arr = jsonObj.getJSONArray("Day");

            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        JSONObject oj = (JSONObject) jsonObj.getJSONArray("Day").get(day);;
        
        JSONArray oj2 = oj.getJSONArray("Records");
        // JSONObject hm = oj2.getJSONObject(0);
        return oj2;
    }

    void createEmpty(String path) throws IOException{
        FileWriter fw;
        try{
            fw = new FileWriter(new File(path));
        }catch(Exception e){
            File tmp = new File(path);
            if(!tmp.exists())
                tmp.getParentFile().mkdirs();
            fw = new FileWriter(new File(path));
        }
        JSONObject json = new JSONObject();
        String date = EnDecoder.Decode( path.substring(path.length()-11, path.length()-5) );
        json.put("Month", date);
        json.put("Day", new JSONArray());
        fw.write(json.toString());
        fw.close();
    }

    private void SaveDayData(int day, JSONArray newData){
        JSONObject j = new JSONObject();
        j.put("Records", newData );
        jsonObj.getJSONArray("Day").put(day-1, j);
    }

    void DeleteEvent(int day, String AbsID){
        JSONArray arr = getDayData(day);
        for(int i = 0; i < arr.length(); ++i){
            if(arr.getJSONObject(i).getString("AbsID").equals( AbsID ) ) {
                arr.remove(i);
            }
        }
        SaveDayData(day, arr);
    }
    void addNewEvent(JSONObject json) throws IOException{
        int day = json.getInt("Day");
        JSONArray arr = getDayData(day);
        ArrayList<EventLine> al = new ArrayList<EventLine>();
        al.add(new EventLine(-1, json));
        for(int i = 0; i < arr.length(); ++i){
            al.add(new EventLine(i, arr.getJSONObject(i)));
        }
        Collections.sort(al);
        JSONArray tmp = new JSONArray();
        for(int i = 0; i < al.size(); ++i)
            if(al.get(i).index != -1)
                tmp.put(arr.getJSONObject(al.get(i).index));
            else tmp.put(json);
        SaveDayData(day, tmp);

    }

    void saveToFile(){
        FileWriter fw;
        try {
            fw = new FileWriter(new File(filePath));
            fw.write(jsonObj.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println("結果儲存失敗");
        }
        try{
            load();
        }catch(Exception e){
            System.out.println("讀取失敗");
        }
        
    }


}





class EventLine implements Comparable<EventLine>{
    int index, s, e, priority, Allday;
    EventLine(int i, int ss, int ee, int pp, int all){
        index = i;
        s = ss;
        e = ee;
        priority = pp;
        Allday = all;
    }
    EventLine(int i, JSONObject json){
        index = i;
        try{
            s = json.getInt("StartTime");
            e = json.getInt("EndTime");
        }catch(Exception ee){
           System.out.println("起始時間資料格式錯誤");
           s = 0;
           e = 0;
        }
        priority = json.getInt("Priority");
        Allday = json.getInt("Allday");
    }

    @Override
    public int compareTo(EventLine o) {
        if(priority > o.priority) return 1;
        if(priority < o.priority) return -1;
        if(Allday > o.Allday) return 1;
        if(Allday < o.Allday) return -1;
        if(s > o.s) return 1;
        if(s < o.s) return -1;
        if(e > o.e) return 1;
        if(e < o.e) return -1;
        return 0;
        
    }
    
}
