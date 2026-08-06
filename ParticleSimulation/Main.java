package ParticleSimulation;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker; // Added ColorPicker
import javafx.scene.paint.Color;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import Shapes.Square;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 700;

    private int numberOfParticles = 0;
    private int hue = 0;
    private int TotalParticles = 0;
    private boolean isSpawning = false;
    private boolean isReplaying = false;
    private boolean isFillingSquare = false;
    private double SquareX = 100;
    private double SquareY = 50;
    private double SquareSize = 400;
    private double frameCount = 0;
    private double fpsFrames = 0;
    private long lastFpsTime = 0;
    public static double particleRadius = 5;
    public static Group group = new Group();
    private PhysicsWorld physicsSimulation = new PhysicsWorld();
    private Random random = new Random();
    private List<Color> savedColors = new ArrayList<>();
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Text uiText = new Text(20, 30, "Particles: 0");
        uiText.setFill(Color.WHITE);
        uiText.setFont(new Font(20));

        Text fpsText = new Text(20, HEIGHT - 20, "FPS: 0");
        fpsText.setFill(Color.WHITE);
        fpsText.setFont(new Font(20));

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.WHITE); 
        colorPicker.setLayoutX(WIDTH - 150); 
        colorPicker.setLayoutY(15);

        Button clearButton = new Button("Clear Particles");
        clearButton.setLayoutX(WIDTH - 150);
        clearButton.setLayoutY(50);

        Button saveColor = new Button("Save Particle Color");
        saveColor.setLayoutX(WIDTH - 150);
        saveColor.setLayoutY(85);

        Button replayParticles = new Button("Replay Particle simulation");
        replayParticles.setLayoutX(WIDTH - 150);
        replayParticles.setLayoutY(120);

        Button fillSquareButton = new Button("Fill Square with Particles");
        fillSquareButton.setLayoutX(WIDTH - 150);
        fillSquareButton.setLayoutY(155);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        group.getChildren().addAll(canvas, fpsText, uiText, colorPicker, clearButton, saveColor, replayParticles, fillSquareButton);
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.BLACK);

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isSpawning = true;
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isSpawning = false;
            }
      
        });

        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                Color selectedColor = colorPicker.getValue();
                double mouseX = event.getX();
                double mouseY = event.getY();
                for (Particle p : physicsSimulation.getParticles()) {
 
                    double dx = mouseX - p.getCenterX();
                    double dy = mouseY - p.getCenterY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance <= particleRadius) {
                        p.setColor(selectedColor);
                    }
                }
            }
        });

        clearButton.setOnAction(event -> {
            physicsSimulation.clearParticles();
            numberOfParticles = 0;
        });

        saveColor.setOnAction(event -> {
            savedColors.clear(); 
            for (Particle p : physicsSimulation.getParticles()) {
                savedColors.add(p.getColor());
            }
        });

        replayParticles.setOnAction(event -> {
            physicsSimulation.clearParticles();
            numberOfParticles = 0;
            isReplaying = true;
        });

        fillSquareButton.setOnAction(event -> {
            isFillingSquare = true;
        });



        
        Square newSquare = new Square(SquareX, SquareY, SquareSize, Color.GREEN);
        newSquare.drawSquare();
        physicsSimulation.addShape(newSquare);
        TotalParticles = calculateMaxCircles(SquareSize, particleRadius);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCanvas(gc);
                updateFPS(now, fpsText);
                if ((isSpawning || isReplaying || isFillingSquare) && frameCount % 5 == 0) {
                    for (int i = 0; i < 5; i++) {
                        createPartical(SquareX + particleRadius, SquareY + 15 + 2 * i * particleRadius, 1, particleRadius);
                    }
                }
                
                physicsSimulation.updatePhysics(30);
                uiText.setText("Particles: "+ numberOfParticles);
            }
        };
        timer.start();

        primaryStage.setTitle("Circle Physics Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void updateCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        physicsSimulation.drawParticles(gc);
    }

    public void updateFPS(long now, Text fpsText) {
        frameCount++;
        fpsFrames++;

        if (lastFpsTime == 0) {
            lastFpsTime = now;
        }

        if (now - lastFpsTime >= 1_000_000_000L) {
            int fps = (int) (fpsFrames * 1_000_000_000L / (now - lastFpsTime));
            fpsText.setText("FPS: " + fps);
            fpsFrames = 0;
            lastFpsTime = now;
        }
    }


    public void createPartical(double locationX, double locationY, double mass, double radius){  
       
        if (numberOfParticles >= TotalParticles) {
            isFillingSquare = false;
            return; 
        }    
        Color particleColor = getParticleColor();
        Particle partical = new Particle(locationX, locationY, radius, mass, particleColor);
        partical.setVelocity(10,1);
        physicsSimulation.addParticle(partical);
        numberOfParticles++;      
    }

    public Color getParticleColor() {
        if (isReplaying) {
            if (numberOfParticles < savedColors.size()-1) {
                return savedColors.get(numberOfParticles);
            } else {
                isReplaying = false; 
            }
        }
        return Color.WHITE;
    }

    public static int calculateMaxCircles(double boxLength, double radius) {
        if (boxLength < 2 * radius) {
            return 0;
        }

        double diameter = 2 * radius;

        int oddRowCapacity = (int) Math.floor(boxLength / diameter);
        int evenRowCapacity = (int) Math.floor((boxLength - radius) / diameter);
        double verticalSpacing = radius * Math.sqrt(3);
        double heightAvailable = boxLength - diameter; 
        
        int totalRows = 1 + (int) Math.floor(heightAvailable / verticalSpacing);
        int numOddRows = (int) Math.ceil((double) totalRows / 2);
        int numEvenRows = (int) Math.floor((double) totalRows / 2);
        int totalCircles = (numOddRows * oddRowCapacity) + (numEvenRows * evenRowCapacity);

        return totalCircles;
    }

}