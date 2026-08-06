package ParticleSimulation;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class Surface extends Line {
    
    private Vector normalVector;
    private Vector tangentVector;
    private double thickness;

  


    public Surface(double startX, double startY, double endX, double endY, double thickness, Color color) {
        super(startX, startY, endX, endY);
        setStroke(color);
        this.thickness = thickness;
        tangentVector = new Vector(endX - startX, endY - startY);
        normalVector = tangentVector.rotate90Degrees().normalize();
        setStrokeWidth(thickness);
    }

    public double getSurfaceNormalX() {
        return normalVector.getX();
    }

    public double getSurfaceNormalY() {
        return normalVector.getY();
    }

    public double getSurfaceTangentX() {
        return tangentVector.getX();
    }

    public double getSurfaceTangentY() {
        return tangentVector.getY();
    }

    public Vector getSurfaceNormal() {
        return normalVector;
    }

    public Vector getSurfaceTangent() {
        return tangentVector;
    }

    public boolean isColliding(Particle particle) {
        double centerX = particle.getCenterX();
        double centerY = particle.getCenterY();
        double radius = particle.getRadius();

        double surfaceLengthSquared = (tangentVector.getX() * tangentVector.getX()) + (tangentVector.getY() * tangentVector.getY());
        double t = tangentVector.dot(centerX - getStartX(), centerY - getStartY()) / surfaceLengthSquared;

        if (t < 0 || t > 1) {
            return false;
        }

        double closestPointX = getStartX() + t * tangentVector.getX() - thickness / 2 * normalVector.getX();
        double closestPointY = getStartY() + t * tangentVector.getY() - thickness / 2 * normalVector.getY();

        double distanceSquared = Math.pow(centerX - closestPointX, 2) + Math.pow(centerY - closestPointY, 2);

        return distanceSquared  <= radius * radius;
    }

    
}
