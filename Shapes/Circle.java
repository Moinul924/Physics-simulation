package Shapes;
import ParticleSimulation.Main;
import ParticleSimulation.Surface;
import javafx.scene.paint.Color;

public class Circle extends Shape {

    private double radius;
    private double boundingBoxStartX;
    private double boundingBoxStartY;

    public Circle(double centerX,double centerY,double radius,Color color){
        
        super(centerX, centerY, 2 * radius, color);
        this.radius = radius;
        this.boundingBoxStartX = centerX-radius; 
        this.boundingBoxStartY = centerY-radius;
    }

    public void drawCircle(int sides){
        double angleIncrement = 2 * Math.PI / sides;
        for(int i= 0;i<sides;i++){
            double angle1 = i*angleIncrement;
            double angle2 = (i+1)*angleIncrement;
            double x1 = this.x + radius*Math.cos(angle1);
            double y1 = this.y + radius*Math.sin(angle1);
            double x2 = this.x + radius*Math.cos(angle2);
            double y2 = this.y + radius*Math.sin(angle2);
            Surface surface = new Surface(x1, y1, x2, y2, 0 , color);
            surfaces.add(surface);
            Main.group.getChildren().add(surface);
        }
    }

    public double getCenterX() {
        return this.x;
    }

    public double getCenterY() {
        return this.y;
    }

    public double getRadius() {
        return radius;
    }

    public double getBoundingBoxStartX() {
        return boundingBoxStartX;
    }

    public double getBoundingBoxStartY() {
        return boundingBoxStartY;
    }




    
}
