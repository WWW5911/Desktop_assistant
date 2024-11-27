import java.util.ArrayList;

import javafx.scene.layout.Pane;

public class clouds {
    ArrayList<cloud> al;
    double maxWidth, maxHeight, emptyspace;
    clouds(int size, Point center, Pane root, double maxW, double maxH, double rangeX, double rangeY){
        al = new ArrayList<cloud>();
        maxWidth = maxW;
        maxHeight = maxH;
        emptyspace = 100;
        for(int i = 0; i < size; ++i){
            double x , y ;
            do{
                if(Math.random() < 0.5) x = -1;
                else x = 1;
                if(Math.random() < 0.5) y = -1;
                else y = 1;
                x = x *  ( Math.random() * (rangeX) + 100 ) + center.x ;
                y = y *  (Math.random() * (rangeY-100) + 50) + center.y -150;    

            }while(!checkOverlappping(x, y));
            al.add( new cloud(x, y, root) );
        }
        System.out.println("");
    }
    private boolean checkOverlappping(double x, double y){
        for(int i = 0; i < al.size(); ++i){
            if (al.get(i).cord.distence(x, y) < 200)
                return false;
        }
        return true;
    }
    public void show(){
        for(int i = 0; i < al.size(); ++i)
            al.get(i).show(maxWidth, maxHeight);
    }
}
