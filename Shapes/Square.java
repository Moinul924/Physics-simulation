package Shapes;
import javafx.scene.paint.Color;
import java.util.List;

import ParticleSimulation.Main;
import ParticleSimulation.Surface;


public class Square extends Shape {

    public Square(double startX,double startY,double length,Color color){
        super(startX, startY, length, color);
    }

    public void drawSquare(){
        double[] xCorners = {this.x, this.x + length, this.x + length, this.x};
        double[] yCorners = {this.y, this.y, this.y + length, this.y + length};

        for(int i = 0; i < 4;i++){
            double x1 = xCorners[i];
            double y1 = yCorners[i];
            double x2 = xCorners[(i + 1) % 4];
            double y2 = yCorners[(i + 1) % 4];

            Surface surface = new Surface(x1, y1, x2, y2, 1, color);
            surfaces.add(surface);
            Main.group.getChildren().add(surface);
        }
    }

    public double getStartX(){
        return this.x;
    }

    public double getStartY(){
        return this.y;
    }

    public double getLength(){
        return length;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
    
}
