
import java.util.Random;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.CubicCurveTo;

import javafx.util.Duration;

public class cloud {
    String file_path;
    double x, y;
    Point cord;
    MyImage mImage;
    PathTransition pt;
    cloud(double xx, double yy, Pane root){
        x = xx;
        y = yy;
        cord = new Point(xx, yy);
        file_path = "75.png";
        mImage = new MyImage(file_path, 200);
        mImage.show(root);

    }
    void show(double rWidth, double rHeight){
        Random rand = new Random();
        Point cur = new Point(x, rHeight);
        double width = rWidth/3;
        Path path = new Path(); 
        int Numcurve = 1;
        path.getElements().add(new MoveTo(x, cur.y));
        for(int i = 0; i < Numcurve; ++i){
            Point next = new Point(x, cur.y - (rHeight- y) / (Numcurve+1) );
            double nextstop = x+rand.nextInt((int)width*2)-width;
            path.getElements().add(new CubicCurveTo(cur.x, cur.y, nextstop, cur.y-(cur.y - next.y)/2 , next.x, next.y));
            cur = next;
        }
        path.getElements().add(new CubicCurveTo(cur.x, cur.y, x+rand.nextInt((int)width*2)-width, cur.y-(cur.y - y)/2, x, y));
        PathTransition pt = new PathTransition();
        pt.setDuration(Duration.millis(1000));
        pt.setPath(path);
        pt.setNode(mImage.imageView);
        pt.play();
        
    }
}

