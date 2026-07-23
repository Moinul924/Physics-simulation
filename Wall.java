import javafx.scene.shape.Line;


public class Wall extends Line {
    
    private double wallNormalX;
    private double wallNormalY;

    private double wallVectorX;
    private double wallVectorY;

  


    public Wall(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    
        
        wallVectorX = endX - startX;
        wallVectorY = endY - startY;
        double wallLength = Math.hypot(wallVectorX, wallVectorY);
        
        wallNormalX = wallVectorY/wallLength;
        wallNormalY = -wallVectorX/wallLength;
    }

    public double getWallNormalX() {
        return wallNormalX;
    }

    public double getWallNormalY() {
        return wallNormalY;
    }

    public double getWallVectorX() {
        return wallVectorX;
    }

    public double getWallVectorY() {
        return wallVectorY;
    }


    public boolean isColliding(PhysicsCircle circle) {
        double circleX = circle.getCenterX();
        double circleY = circle.getCenterY();
        double radius = circle.getRadius();

        double wallLengthSquared = wallVectorX * wallVectorX + wallVectorY * wallVectorY;
        double t = ((circleX - getStartX()) * wallVectorX + (circleY - getStartY()) * wallVectorY) / wallLengthSquared;

        if (t < 0 || t > 1) {
            return false;
        }

        double closestPointX = getStartX() + t * wallVectorX;
        double closestPointY = getStartY() + t * wallVectorY;

        double distanceSquared = (circleX - closestPointX) * (circleX - closestPointX)
                + (circleY - closestPointY) * (circleY - closestPointY);

        return distanceSquared <= radius * radius;
    }

    
}
