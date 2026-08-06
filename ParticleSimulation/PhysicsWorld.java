package ParticleSimulation;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import Shapes.Shape;




public class PhysicsWorld {

    private List<Particle> particles = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();
    private double gravity = 0.1;

    public PhysicsWorld(){
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void clearParticles() {
        particles.clear();
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void drawParticles(GraphicsContext gc) {
        for (Particle particle : particles) {
            gc.setFill(particle.getColor());
            gc.fillOval(particle.getCenterX() - particle.getRadius(), particle.getCenterY() - particle.getRadius(), 2 * particle.getRadius(), 2 * particle.getRadius());
        }
    }


    public void updatePhysics(int subSteps) {
        double deltaTime = 1.0 / subSteps;
        for (int i = 0; i < subSteps; i++) {
            updatePositionAndApplyGravity(deltaTime);
            partitionSpace();
            checkParticleCollisions();
            checkSurfaceCollisions();
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
            double particleVelocityX = Math.abs(particle.getVelocityX()) < 0.3 ? 0 : particle.getVelocityX();
            double particleVelocityY = Math.abs(particle.getVelocityY()) < 0.3 ? 0 : particle.getVelocityY();
            particle.setVelocityY(particle.getVelocityY() + gravity * deltaTime);
            particle.setCenterX(particle.getCenterX() + particleVelocityX * deltaTime);
            particle.setCenterY(particle.getCenterY() + particleVelocityY * deltaTime);

        }
    }

    public void checkSurfaceCollisions() {
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
                    double impactVectorX = dx;
                    double impactVectorY = dy;
                    resolveOverlappingParticles(particle1, particle2, impactVectorX, impactVectorY);
                }
            }
        }
    }

    public void checkParticleCollisions() {
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
    
    public void resolveWallOverlap(Particle particle, Surface surface) {
        double normalX = -surface.getSurfaceNormalX();
        double normalY = -surface.getSurfaceNormalY();
        

        // Nudge the particle out along the normal until it is physically out of the surface
        while (surface.isColliding(particle)) {
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
    
    public void bounceOffSurface(Particle particle, Surface surface) {
        double normalX = -surface.getSurfaceNormalX();
        double normalY = -surface.getSurfaceNormalY();
        
        double speedAlongNormal = particle.getVelocityX() * normalX + particle.getVelocityY() * normalY;
        double speedAlongTangent = particle.getVelocityX() * normalY - particle.getVelocityY() * normalX;
        
        if (speedAlongNormal < 0) {
            double restitution = particle.getCoefficientOfRestitution();
            double newSpeedAlongNormal = -speedAlongNormal * restitution;
            
            double newVelocityX = (newSpeedAlongNormal * normalX) + (speedAlongTangent * normalY);
            double newVelocityY = (newSpeedAlongNormal * normalY) + (speedAlongTangent * -normalX);
            
            particle.setVelocity(newVelocityX, newVelocityY);
        }   
    }
    
    public void resolveParticleCollision(Particle particle1, Particle particle2) {
        double impactVX = particle2.getCenterX() - particle1.getCenterX();
        double impactVectorY = particle2.getCenterY() - particle1.getCenterY();
        resolveOverlappingParticles(particle1, particle2, impactVX, impactVectorY);
        double distanceSquared = impactVX * impactVX + impactVectorY * impactVectorY;    
            
        double[] finalVelocityparticle1 = calculateFinalVelocity(particle1, particle2, impactVX, impactVectorY, distanceSquared);
        double[] finalVelocityparticle2 = calculateFinalVelocity(particle2, particle1, -impactVX, -impactVectorY, distanceSquared);
        particle1.setVelocity(finalVelocityparticle1[0], finalVelocityparticle1[1]);
        particle2.setVelocity(finalVelocityparticle2[0], finalVelocityparticle2[1]);
        
    }
    
    public void resolveOverlappingParticles(Particle particle1, Particle particle2,double impactVX, double impactVectorY) {
        double distance = Math.sqrt(impactVX * impactVX + impactVectorY * impactVectorY);
        if (distance == 0) return;
        double overlap = (particle1.getRadius() + particle2.getRadius()) - distance;
        
        if (overlap > 0) {

            double relaxation = 0.8;
            double separationX = (impactVX / distance) * (overlap / 2)*relaxation;
            double separationY = (impactVectorY / distance) * (overlap / 2)*relaxation;
            
            particle1.setCenterX(particle1.getCenterX() - separationX);
            particle1.setCenterY(particle1.getCenterY() - separationY);
            particle2.setCenterX(particle2.getCenterX() + separationX);
            particle2.setCenterY(particle2.getCenterY() + separationY);
        }
    }
    
    public double[] calculateFinalVelocity(Particle targetparticle, Particle otherparticle,double  impactVX,double impactVectorY, double distanceSquared) {     
        double restitution = targetparticle.getCoefficientOfRestitution() * otherparticle.getCoefficientOfRestitution();  
        double relativeVelocityX = targetparticle.getVelocityX() - otherparticle.getVelocityX();
        double relativeVelocityY = targetparticle.getVelocityY() - otherparticle.getVelocityY();

        double dotProduct = relativeVelocityX * impactVX + relativeVelocityY * impactVectorY;
       
        if (dotProduct < 0) {
            return new double[]{targetparticle.getVelocityX(), targetparticle.getVelocityY()};
        }
        
     
        double totalMass = targetparticle.getMass() + otherparticle.getMass();
        
        // FIX 3: Use (1 + restitution) to correctly apply the bounce multiplier.
        double massRatio = (1 + restitution) * otherparticle.getMass() / totalMass;
        
        double scalar = massRatio * (dotProduct / distanceSquared);
        double velocityChangeX = scalar * impactVX;
        double velocityChangeY = scalar * impactVectorY;
        
        return new double[]{targetparticle.getVelocityX() - velocityChangeX, targetparticle.getVelocityY() - velocityChangeY};
    }
        
    
    
    
    public void checkIfEnergyIsLost() {
        double totalKineticEnergy = 0;
        for (Particle particle : particles) {
            totalKineticEnergy += 0.5 * particle.getMass() * (Math.pow(particle.getVelocityX(), 2) + Math.pow(particle.getVelocityY(), 2));
            
        }
        System.out.println("Total Kinetic Energy: " + totalKineticEnergy);
    }
    
    
    
    
}