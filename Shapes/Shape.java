package Shapes;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

import ParticleSimulation.Main;
import ParticleSimulation.Particle;
import ParticleSimulation.Surface;


public class Shape {
    protected double x; 
    protected double y; 
    protected Color color;
    protected double length;
    protected List<Surface> surfaces = new ArrayList<>();
    protected int partitionSize;
    protected List<Particle>[][] gridPartition; 

    public Shape(double x, double y, double length, Color color) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.color = color;
        this.partitionSize = (int) Math.ceil(this.length / (2 * Main.particleRadius));
        
        // 2. Initialize the 2D array of Lists
        this.gridPartition = new ArrayList[this.partitionSize][this.partitionSize];
        for (int i = 0; i < this.partitionSize; i++) {
            for (int j = 0; j < this.partitionSize; j++) {
                this.gridPartition[i][j] = new ArrayList<>();
            }
        }
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }

    public void addParticleToGrid(Particle particle) {
        int gridX = (int) ((particle.getCenterX() - x) / (2*Main.particleRadius));
        int gridY = (int) ((particle.getCenterY() - y) / (2*Main.particleRadius));

        if (gridX >= 0 && gridX < partitionSize && gridY >= 0 && gridY < partitionSize) {
            gridPartition[gridX][gridY].add(particle); // ADD INSTEAD OF OVERWRITE
        }
    }

    public void clearGrid() {
        for (int i = 0; i < partitionSize; i++) {
            for (int j = 0; j < partitionSize; j++) {
                gridPartition[i][j].clear(); 
            }
        }
    }

    public List<Particle>[][] getGridPartition() {
        return gridPartition;
    }
    
}
