import java.util.List;

public class Physics {

    private List<PhysicsCircle> circles;
    private List<Wall> walls;
    private double gravity = 0.1;

    public Physics(List<PhysicsCircle> circles, List<Wall> walls) {
        this.circles = circles;
        this.walls = walls;
    }


    public void addCircle(PhysicsCircle circle) {
        circles.add(circle);
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public void updatePhysics(int subSteps) {
        double deltaTime = 1.0 / subSteps;
        for (int i = 0; i < subSteps; i++) {
            updatePossitionAndApplyGravity(deltaTime);
            for(int j = 0; j < 5; j++){
                minimizeOverlap();
            }
            checkWallCollisions();
            checkCircleCollisions();
            minimizeOverlap();
            
        }
    }
    
    
    public void updatePossitionAndApplyGravity(double deltaTime) {
        for (PhysicsCircle circle : circles) {
            
            circle.setVelocityY(circle.getVelocityY() + gravity * deltaTime);
            circle.setCenterX(circle.getCenterX() + circle.getVelocityX()*deltaTime);
            circle.setCenterY(circle.getCenterY() + circle.getVelocityY()*deltaTime);
        }
    }

    public void checkWallCollisions() {
        for (PhysicsCircle circle : circles) {
            for (Wall wall : walls) {
                if (wall.isColliding(circle)) {
                    resolveWallOverlap(circle, wall);
                    bounce(circle, wall);
                }
            }
        }
    }

    public void minimizeOverlap(){
        for (PhysicsCircle circle : circles) {
            for (Wall wall : walls) {
                if (wall.isColliding(circle)) {
                    resolveWallOverlap(circle, wall);
                }
            }
        }
        for (int i = 0; i < circles.size(); i++) {
            PhysicsCircle circle1 = circles.get(i);
            
            for (int j = i + 1; j < circles.size(); j++) {
                PhysicsCircle circle2 = circles.get(j);
                
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                
                double distanceSquared = (dx * dx) + (dy * dy);
                double radiusSum = circle1.getRadius() + circle2.getRadius();
                
                if (distanceSquared <= (radiusSum * radiusSum)) {
                    Vector impactVector = new Vector(dx, dy);
                    resolveOverlappingCircles(circle1, circle2, impactVector);
                }
            }
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
                    //checkIfEnergyIsLost();
                }
            }
        }
    }
    
    public void resolveWallOverlap(PhysicsCircle circle, Wall wall) {
        double normalX = wall.getWallNormalX();
        double normalY = wall.getWallNormalY();
        
        Vector circleToWall = new Vector(circle.getCenterX() - wall.getStartX(), circle.getCenterY() - wall.getStartY());
        
        if (circleToWall.dot(normalX, normalY) < 0) {
            normalX = -normalX;
            normalY = -normalY;
        }
        
        // Nudge the circle out along the normal until it is physically out of the wall
        while (wall.isColliding(circle)) {
            circle.setCenterX(circle.getCenterX() + normalX * 0.5);
            circle.setCenterY(circle.getCenterY() + normalY * 0.5);
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
        Vector impactVector = new Vector(circle2.getCenterX() - circle1.getCenterX(),
        circle2.getCenterY() - circle1.getCenterY());
        double distanceSquared = Math.pow(impactVector.getX(), 2) + Math.pow(impactVector.getY(), 2);    
        
        Vector finalVelocityCircle1 = claculateFinalVelocity(circle1, circle2, impactVector,distanceSquared);
        Vector finalVelocityCircle2 = claculateFinalVelocity(circle2, circle1, impactVector.scale(-1),distanceSquared);
        circle1.setVelocity(finalVelocityCircle1.getX(), finalVelocityCircle1.getY());
        circle2.setVelocity(finalVelocityCircle2.getX(), finalVelocityCircle2.getY());
        
    }
    
    public void resolveOverlappingCircles(PhysicsCircle circle1, PhysicsCircle circle2,Vector impactVector) {
        double distance = impactVector.magnitude();
        double overlap = (circle1.getRadius() + circle2.getRadius()) - distance;
        
        if (overlap > 0) {
            double separationX = (impactVector.getX() / distance) * (overlap / 2);
            double separationY = (impactVector.getY() / distance) * (overlap / 2);
            
            circle1.setCenterX(circle1.getCenterX() - separationX);
            circle1.setCenterY(circle1.getCenterY() - separationY);
            circle2.setCenterX(circle2.getCenterX() + separationX);
            circle2.setCenterY(circle2.getCenterY() + separationY);
        }
    }
    
    public Vector claculateFinalVelocity(PhysicsCircle targetCircle,PhysicsCircle otherCircle,Vector impactVector,double distanceSquared){     
        double restitution = targetCircle.getCoefficientOfRestitution() * otherCircle.getCoefficientOfRestitution();  
        Vector relativeVelocity = targetCircle.getVelocity().subtract(otherCircle.getVelocity());
        if(relativeVelocity.magnitude() < 0.5){
            return targetCircle.getVelocity();
        }
        double totalMass = targetCircle.getMass()+otherCircle.getMass();
        double dotProduct = relativeVelocity.dot(impactVector.getX(),impactVector.getY());
        double massRatio = (1-restitution) * otherCircle.getMass() / totalMass;
        double scalar =massRatio*(dotProduct/distanceSquared);
        Vector velocityChange = impactVector.scale(scalar);
        return targetCircle.getVelocity().subtract(velocityChange);
    }
        
    
    
    
    public void checkIfEnergyIsLost() {
        double totalKineticEnergy = 0;
        for (PhysicsCircle circle : circles) {
            totalKineticEnergy += 0.5 * circle.getMass() * (Math.pow(circle.getVelocityX(), 2) + Math.pow(circle.getVelocityY(), 2));
            
        }
        System.out.println("Total Kinetic Energy: " + totalKineticEnergy);
    }
    
    
    
    
}