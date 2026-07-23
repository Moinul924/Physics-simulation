import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PhysicsCircle extends Circle {
    private double velocityX;
    private double velocityY;
    private double mass;
    private double coefficientOfRestitution;

    public PhysicsCircle(double centerX, double centerY, double radius, double mass, Color color) {
        super(centerX, centerY, radius);
        setFill(color);
        this.velocityX = 0;
        this.velocityY = 0;
        this.mass = mass;
        this.coefficientOfRestitution = 0.5;

    }

    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }


}
