package ParticleSimulation;
import javafx.scene.paint.Color;


public class Particle {
    private double velocityX;
    private double velocityY;
    private double mass;
    private double coefficientOfRestitution;
    private double radius;
    private double centerX;
    private double centerY;
    private Color color;


    public Particle(double centerX, double centerY, double radius, double mass, Color color) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
        this.velocityX = 0;
        this.velocityY = 0;
        this.mass = mass;
        this.coefficientOfRestitution = 0.5;

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) { 
        this.color = color; 
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
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


    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getCoefficientOfRestitution() {
        return coefficientOfRestitution;
    }

    public double getMass() {
        return mass;
    }


}