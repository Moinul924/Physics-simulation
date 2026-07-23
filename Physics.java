import java.util.List;
import java.util.ArrayList;


public class Physics {

    ArrayList<PhysicsCircle> circles;
    ArrayList<Wall> walls = new ArrayList<>();

    Physics(ArrayList<PhysicsCircle> circles, ArrayList<Wall> walls) {
        this.circles = circles;
        this.walls = walls;
    }

    public void applyVelocity() {
        for (PhysicsCircle circle : circles) {
            for(Wall wall:walls){
                if(wall.isColliding(circle)){
                
                    bounce(circle, wall);
                }
            }
            circle.setCenterX(circle.getCenterX() + circle.getVelocityX());
            circle.setCenterY(circle.getCenterY() + circle.getVelocityY());

        }
    }

    public void bounce(PhysicsCircle circle, Wall wall) {
        double normalX = wall.getWallNormalX();
        double normalY = wall.getWallNormalY();
        double dx = circle.getCenterX() - wall.getStartX();
        double dy = circle.getCenterY() - wall.getStartY();
        
        
        
        if ((dx * normalX + dy * normalY) < 0) {
           
            normalX = -normalX;
            normalY = -normalY;
        }
        double circleVelocityX = circle.getVelocityX();
        double circleVelocityY = circle.getVelocityY();

        double speedAlongNormal = circleVelocityX * normalX + circleVelocityY * normalY;
        double speedAlongTangent = circleVelocityX * normalY + circleVelocityY * -normalX;

        if(speedAlongNormal < 0) {
            double newSpeedAlongNormal = -speedAlongNormal * circle.getCoefficientOfRestitution();
            double newVelocityX = newSpeedAlongNormal * normalX + speedAlongTangent * normalY;
            double newVelocityY = newSpeedAlongNormal * normalY + speedAlongTangent * -normalX;

            circle.setVelocity(newVelocityX, newVelocityY);
        }

    }


    
}
