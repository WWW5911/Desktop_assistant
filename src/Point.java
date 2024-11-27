public class Point {
    double x, y;
    Point(){
        x = 0;
        y = 0;
    }
    Point(double xx, double yy){
        x = xx;
        y = yy;
    }
    public double distence(Point p2){
        return Math.sqrt(  (x - p2.x) * (x - p2.x) + (y - p2.y) * (y - p2.y) );
    }
    public double distence(double xx, double yy){
        return Math.sqrt(  (x - xx) * (x - xx) + (y - yy) * (y - yy) );
    }
}
