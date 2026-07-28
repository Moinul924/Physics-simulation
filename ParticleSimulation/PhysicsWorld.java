package ParticleSimulation;
import java.util.ArrayList;
import java.util.List;

import Shapes.Shape;


public class PhysicsWorld {

    private List<Particle> particles = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();
    private double gravity = 0.1;

    public PhysicsWorld(){
    }


    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }


    public void updatePhysics(int subSteps) {
        double deltaTime = 1.0 / subSteps;
        for (int i = 0; i < subSteps; i++) {
            updatePositionAndApplyGravity(deltaTime);
            checkSurfaceCollisions();
            checkParticleCollisions();
        }
    }
    
    public void partitionSpace(){
        for(Shape shape:shapes){
            shape.clearGrid();
            for (Particle particle : particles) {
                shape.addParticleToGrid(particle);
            }
        }
    }
    
    
    
    public void updatePositionAndApplyGravity(double deltaTime) {
        for (Particle particle : particles) { 
            particle.setVelocityY(particle.getVelocityY() + gravity * deltaTime);
            particle.setCenterX(particle.getCenterX() + particle.getVelocityX()*deltaTime);
            particle.setCenterY(particle.getCenterY() + particle.getVelocityY()*deltaTime);
        }
    }

    public void checkSurfaceCollisions() {
        partitionSpace();
        for(Shape shape : shapes){   
            List<Surface> surfaces = shape.getSurfaces(); 
            for (Particle particle : particles) {
                for (Surface surface : surfaces) {
                    if (surface.isColliding(particle)) {
                        bounceOffSurface(particle, surface);
                        resolveWallOverlap(particle, surface);
                    }
                }
            }
            //findSurfaceCollisions(shape.getGridPartition(), surfaces);
        }
    }

    public void findSurfaceCollisions(Particle[][] gridPartition, List<Surface> surfaces){
        int gridLength = gridPartition.length-1;
        for(int x= 0;x<gridLength;x++){
            Particle [] grid = {gridPartition[x][0]};
            for(Surface surface:surfaces){
                checkParticaleSurfaceCollision(grid,surface);
            }

        }
        for(int y = 1;y < gridLength;y++){
            Particle [] grid = {gridPartition[gridLength][y]};
            for(Surface surface:surfaces){
                checkParticaleSurfaceCollision(grid,surface);
            }

        }
        for(int x= gridLength;x >= 0;x--){
            Particle [] grid = {gridPartition[x][gridLength]};
            for(Surface surface:surfaces){
                checkParticaleSurfaceCollision(grid,surface);
            }

        }
        for(int y = gridLength-1; y>0;y--){
            Particle [] grid = {gridPartition[0][y]};
            for(Surface surface:surfaces){
                checkParticaleSurfaceCollision(grid,surface);
            }

        }


    }

    public void checkParticaleSurfaceCollision(Particle[] grid, Surface surface) {
        for(Particle particle : grid) {
            if (particle != null &&surface.isColliding(particle)) {
                bounceOffSurface(particle, surface);
                resolveWallOverlap(particle, surface);
            }
        }
    }

    

    public void minimizeOverlap(){
        
        for (int i = 0; i < particles.size(); i++) {
            Particle particle1 = particles.get(i);
            
            for (int j = i + 1; j < particles.size(); j++) {
                Particle particle2 = particles.get(j);
                
                double dx = particle2.getCenterX() - particle1.getCenterX();
                double dy = particle2.getCenterY() - particle1.getCenterY();
                
                double distanceSquared = (dx * dx) + (dy * dy);
                double radiusSum = particle1.getRadius() + particle2.getRadius();
                
                if (distanceSquared <= (radiusSum * radiusSum)) {
                    Vector impactVector = new Vector(dx, dy);
                    resolveOverlappingParticles(particle1, particle2, impactVector);
                }
            }
        }
    }

    public void checkParticleCollisions() {
        partitionSpace();
        for(Shape shape:shapes){
            findGridCollisions(shape.getGridPartition());
        }
    }

    public void findGridCollisions(List<Particle>[][] gridPartition){
        int[][] neighborOffsets = {
            {1, 0}, {-1, 1}, {0, 1}, {1, 1} 
        };
        for(int x = 0; x < gridPartition.length; x++){
            for(int y = 0; y < gridPartition[0].length; y++){
                List<Particle> grid = gridPartition[x][y]; // Now a List
                checkSameGridCollisions(grid);
                for(int[] offset : neighborOffsets){
                    int neighborX = x + offset[0];
                    int neighborY = y + offset[1];
                    if(neighborX >= 0 && neighborX < gridPartition.length && 
                       neighborY >= 0 && neighborY < gridPartition[0].length){
                        List<Particle> neighborGrid = gridPartition[neighborX][neighborY]; // Now a List
                        checkNeighboringGridCollision(grid,neighborGrid);
                    }
                }
            }
        }
    }

    public void checkSameGridCollisions(List<Particle> gridObjects){
        for(int i = 0; i < gridObjects.size(); i++){
            for(int j = i+1; j < gridObjects.size(); j++){
                Particle particle1 = gridObjects.get(i);
                Particle particle2 = gridObjects.get(j);
                if(particle1 == null || particle2 == null){continue;}
                if(particalCollide(particle1, particle2)){
                    resolveParticleCollision(particle1, particle2);
                }
            }
        }
    }

   public void checkNeighboringGridCollision(List<Particle> grid1, List<Particle> grid2){
        for(int i = 0; i < grid1.size(); i++){
            for(int j = 0; j < grid2.size(); j++){
                Particle particle1 = grid1.get(i);
                Particle particle2 = grid2.get(j);
                
                // Keep the particle1 == particle2 safety check here
                if(particle1 == null || particle2 == null || particle1 == particle2){continue;}
                
                if(particalCollide(particle1, particle2)){
                    resolveParticleCollision(particle1, particle2);
                }
            }
        }
    }
    
    public void resolveWallOverlap(Particle particle, Surface wall) {
        double normalX = wall.getWallNormalX();
        double normalY = wall.getWallNormalY();
        
        Vector particleToWall = new Vector(particle.getCenterX() - wall.getStartX(), particle.getCenterY() - wall.getStartY());
        
        if (particleToWall.dot(normalX, normalY) < 0) {
            normalX = -normalX;
            normalY = -normalY;
        }
        
        // Nudge the particle out along the normal until it is physically out of the wall
        while (wall.isColliding(particle)) {
            particle.setCenterX(particle.getCenterX() + normalX * 0.5);
            particle.setCenterY(particle.getCenterY() + normalY * 0.5);
        }
    }

    public boolean particalCollide(Particle particle1,Particle particle2){
        double dx = particle2.getCenterX() - particle1.getCenterX();
        double dy = particle2.getCenterY() - particle1.getCenterY();
                
        double distanceSquared = (dx * dx) + (dy * dy);
        double radiusSum = particle1.getRadius() + particle2.getRadius();
        return distanceSquared <= (radiusSum)*(radiusSum);
    }
    
    public void bounceOffSurface(Particle particle, Surface wall) {
        double normalX = wall.getWallNormalX();
        double normalY = wall.getWallNormalY();
        
        Vector particleToWall = new Vector(particle.getCenterX() - wall.getStartX(), particle.getCenterY() - wall.getStartY());
        
        if (particleToWall.dot(normalX, normalY) < 0) {
            normalX = -normalX;
            normalY = -normalY;
        }
        
        
        
        Vector particleVelocity = particle.getVelocity();
        
        double speedAlongNormal = particleVelocity.dot(normalX, normalY);
        double speedAlongTangent = particleVelocity.dot(-normalY, normalX);
        
        if (speedAlongNormal < 0) {
            double restitution = particle.getCoefficientOfRestitution();
            double newSpeedAlongNormal = -speedAlongNormal * restitution;
            
            double newVelocityX = (newSpeedAlongNormal * normalX) + (speedAlongTangent * normalY);
            double newVelocityY = (newSpeedAlongNormal * normalY) + (speedAlongTangent * -normalX);
            
            particle.setVelocity(newVelocityX, newVelocityY);
        }
    }
    
    public void resolveParticleCollision(Particle particle1, Particle particle2) {
        Vector impactVector = new Vector(particle2.getCenterX() - particle1.getCenterX(),
                                        particle2.getCenterY() - particle1.getCenterY());
        resolveOverlappingParticles(particle1, particle2, impactVector);                                
        double distanceSquared = Math.pow(impactVector.getX(), 2) + Math.pow(impactVector.getY(), 2);    
        
        Vector finalVelocityparticle1 = claculateFinalVelocity(particle1, particle2, impactVector,distanceSquared);
        Vector finalVelocityparticle2 = claculateFinalVelocity(particle2, particle1, impactVector.scale(-1),distanceSquared);
        particle1.setVelocity(finalVelocityparticle1.getX(), finalVelocityparticle1.getY());
        particle2.setVelocity(finalVelocityparticle2.getX(), finalVelocityparticle2.getY());
        
    }
    
    public void resolveOverlappingParticles(Particle particle1, Particle particle2,Vector impactVector) {
        double distance = impactVector.magnitude();
        if (distance == 0) return;
        double overlap = (particle1.getRadius() + particle2.getRadius()) - distance;
        
        if (overlap > 0) {
            double separationX = (impactVector.getX() / distance) * (overlap / 2);
            double separationY = (impactVector.getY() / distance) * (overlap / 2);
            
            particle1.setCenterX(particle1.getCenterX() - separationX);
            particle1.setCenterY(particle1.getCenterY() - separationY);
            particle2.setCenterX(particle2.getCenterX() + separationX);
            particle2.setCenterY(particle2.getCenterY() + separationY);
        }
    }
    
    public Vector claculateFinalVelocity(Particle targetparticle, Particle otherparticle, Vector impactVector, double distanceSquared) {     
        double restitution = targetparticle.getCoefficientOfRestitution() * otherparticle.getCoefficientOfRestitution();  
        Vector relativeVelocity = targetparticle.getVelocity().subtract(otherparticle.getVelocity());
        
        double dotProduct = relativeVelocity.dot(impactVector.getX(), impactVector.getY());
        
        if(relativeVelocity.magnitude() < 0.5){
            return targetparticle.getVelocity();
        }
        // FIX 1: The Separation Check. 
        // If the dot product is less than 0, the particles are already moving apart. 
        // We return immediately so we don't accidentally suck them back together!
        if (dotProduct < 0) {
            return targetparticle.getVelocity();
        }
        
        // FIX 2: We removed the "relativeVelocity.magnitude() < 0.5" block entirely so 
        // resting particles can still gently push off of one another.

        double totalMass = targetparticle.getMass() + otherparticle.getMass();
        
        // FIX 3: Use (1 + restitution) to correctly apply the bounce multiplier.
        double massRatio = (1 + restitution) * otherparticle.getMass() / totalMass;
        
        double scalar = massRatio * (dotProduct / distanceSquared);
        Vector velocityChange = impactVector.scale(scalar);
        
        return targetparticle.getVelocity().subtract(velocityChange);
    }
        
    
    
    
    public void checkIfEnergyIsLost() {
        double totalKineticEnergy = 0;
        for (Particle particle : particles) {
            totalKineticEnergy += 0.5 * particle.getMass() * (Math.pow(particle.getVelocityX(), 2) + Math.pow(particle.getVelocityY(), 2));
            
        }
        System.out.println("Total Kinetic Energy: " + totalKineticEnergy);
    }
    
    
    
    
}