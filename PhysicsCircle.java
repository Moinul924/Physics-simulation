import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PhysicsCircle extends Circle {
    private Vector velocity;
    private double mass;
    private double coefficientOfRestitution;

    public PhysicsCircle(double centerX, double centerY, double radius, double mass, Color color) {
        super(centerX, centerY, radius);
        setFill(color);
        velocity = new Vector(0, 0);
        this.mass = mass;
        this.coefficientOfRestitution = 0.5;

    }

    public void setVelocity(double velocityX, double velocityY) {
        velocity.setX(velocityX);
        velocity.setY(velocityY);
    }

    public double getVelocityX() {
        return velocity.getX();
    }

    public double getVelocityY() {
        return velocity.getY();
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocityX(double velocityX) {
        velocity.setX(velocityX);
    }

    public void setVelocityY(double velocityY) {
        velocity.setY(velocityY);
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }

    public double getMass() {
        return mass;
    }


}
