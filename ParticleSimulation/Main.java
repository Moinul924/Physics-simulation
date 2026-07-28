package ParticleSimulation;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import java.util.ArrayList;
import Shapes.Square;


public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double mouseX;
    private double mouseY;

    private int numberOfParticles = 0;
    private int hue = 0;
    private boolean isSpawning = false;
    private double frameCount = 0;
    public static double particleRadius = 5;
    public static Group group = new Group();
    private PhysicsWorld physicsSimulation = new PhysicsWorld();
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Text uiText = new Text(20, 30, "Particles: 0");
        uiText.setFill(Color.WHITE);
        uiText.setFont(new Font(20));
        group.getChildren().add(uiText);
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.BLACK);

       scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isSpawning = true;
            }
            //createPartical(105, 55, 1, particleRadius); 
 
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isSpawning = false;
            }
        });

        
        Square newSquare = new Square(100, 50, 400, Color.GREEN);
        newSquare.drawSquare();
        physicsSimulation.addShape(newSquare);
        //drawCircle(300,300,250,20,Color.WHITE);

        

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameCount++;
                if (isSpawning && frameCount % 5 == 0) {
                    createPartical(110, 65, 1, particleRadius);
                }
                physicsSimulation.updatePhysics(10);
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


    public void createPartical(double locationX, double locationY, double mass, double radius){      
        Color particleColor = getParticleColor();
        Particle partical = new Particle(locationX, locationY, radius, mass, particleColor);

        partical.setOnMousePressed(event -> {
            mouseX = event.getSceneX() - partical.getCenterX();
            mouseY = event.getSceneY() - partical.getCenterY();
            partical.setVelocity(0, 0);
        });

        partical.setOnMouseDragged(event -> {
            partical.setCenterX(event.getSceneX() - mouseX);
            partical.setCenterY(event.getSceneY() - mouseY);
        });

        partical.setVelocity(1,1);

        group.getChildren().add(partical);
        physicsSimulation.addParticle(partical);
        numberOfParticles++;      
    }

    public Color getParticleColor() {
        if(numberOfParticles % 20 == 0) {
            hue = (hue + 5)% 360;
        }
        Color particleColor = Color.hsb(hue,0.97,0.94);
        return particleColor;
    }


}