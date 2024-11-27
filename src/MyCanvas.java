import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;


public class MyCanvas extends Canvas{

    private GraphicsContext gc;
    private double width, height;
	public MyCanvas(double width, double height){
		super(width, height);
        this.width = width;
        this.height = height;
		
		gc = getGraphicsContext2D();
		drawRandomCurves(gc, new Point(), new Point(1200, 500));
    }
    public void drawRandomCurves(GraphicsContext gc, Point start, Point end){
        gc.save();
        Random rand = new Random();
        Point cur = start, b = new Point(rand.nextInt((int)width), rand.nextInt((int)height))
                        , c = new Point(rand.nextInt((int)b.x), b.y+rand.nextInt((int)(height-b.y)));
        for(double i = 0; i <= 1; i += 0.01){
            Point next = ThirdCurves(start, b, c, end, i);
            gc.strokeLine(cur.x, cur.y, next.x, next.y);
            cur = next;
        }
        gc.restore();
    }
    public Point ThirdCurves(Point a, Point b, Point c, Point d, double t){
        double x = a.x * Math.pow((1 - t), 3) + 3 * b.x * t * Math.pow((1 - t), 2) + c.x * t * t * (1 - t) + d.x * Math.pow( t, 3);
        double y = a.y * Math.pow((1 - t), 3) + 3 * b.y * t * Math.pow((1 - t), 2) + c.y * t * t * (1 - t) + d.y * Math.pow( t, 3);
        return new Point(x, y);
    }
}
