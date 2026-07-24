import java.util.List;

public class Physics {

    private List<PhysicsCircle> circles;
    private List<Wall> walls;

    public Physics(List<PhysicsCircle> circles, List<Wall> walls) {
        this.circles = circles;
        this.walls = walls;
    }

    public void applyVelocity() {
        for (PhysicsCircle circle : circles) {
            // Check for wall collisions
            for (Wall wall : walls) {
                if (wall.isColliding(circle)) {
                    bounce(circle, wall);
                }
            }
            
            // Apply velocity to position
            circle.setCenterX(circle.getCenterX() + circle.getVelocityX());
            circle.setCenterY(circle.getCenterY() + circle.getVelocityY());
        }
    }

    public void checkCircleCollisions() {
        for (int i = 0; i < circles.size(); i++) {
            PhysicsCircle circle1 = circles.get(i);
            
            for (int j = i + 1; j < circles.size(); j++) {
                PhysicsCircle circle2 = circles.get(j);
                
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                
                double distanceSquared = (dx * dx) + (dy * dy);
                double radiusSum = circle1.getRadius() + circle2.getRadius();
                
                if (distanceSquared <= (radiusSum * radiusSum)) {
                    bounceOffCircle(circle1, circle2);
                }
            }
        }
    }

    public void bounce(PhysicsCircle circle, Wall wall) {
        double normalX = wall.getWallNormalX();
        double normalY = wall.getWallNormalY();
        
        Vector circleToWall = new Vector(circle.getCenterX() - wall.getStartX(), circle.getCenterY() - wall.getStartY());

        if (circleToWall.dot(normalX, normalY) < 0) {
            normalX = -normalX;
            normalY = -normalY;
        }
        
        Vector circleVelocity = circle.getVelocity();

        double speedAlongNormal = circleVelocity.dot(normalX, normalY);
        double speedAlongTangent = circleVelocity.dot(-normalY, normalX);

        if (speedAlongNormal < 0) {
            double restitution = circle.getCoefficientOfRestitution();
            double newSpeedAlongNormal = -speedAlongNormal * restitution;
            
            double newVelocityX = (newSpeedAlongNormal * normalX) + (speedAlongTangent * normalY);
            double newVelocityY = (newSpeedAlongNormal * normalY) + (speedAlongTangent * -normalX);

            circle.setVelocity(newVelocityX, newVelocityY);
        }
    }

    public void bounceOffCircle(PhysicsCircle circle1, PhysicsCircle circle2) {
        
    }

    
}