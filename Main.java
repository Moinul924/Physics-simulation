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

import java.util.Random;
import java.util.ArrayList;


public class Main extends Application {

    private static Random random = new Random();
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double mouseX;
    private double mouseY;

    private ArrayList<PhysicsCircle> circles = new ArrayList<>();
    private ArrayList<Wall> walls = new ArrayList<>();
    private Physics physics = new Physics(circles, walls);
    private Group group = new Group();
    private int numberOfParticles = 0;
    private int hue = 0;
    private boolean isSpawning = false;
    private double frameCount = 0;
   

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
 
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isSpawning = false;
            }
        });

        

        drawSquare(100, 50, 400, Color.GREEN);

        

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameCount++;
                if (isSpawning && frameCount % 5 == 0) {
                    createPartical(150, 70, 1, 10); 
                }
                physics.updatePhysics(10);
                uiText.setText("Particals: "+ numberOfParticles);
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

    public void drawSquare(double startX,double startY,double size,Color color){
        
        double[] xCorners = {startX, startX + size, startX + size, startX};
        double[] yCorners = {startY, startY, startY + size, startY + size};

        for(int i = 0; i < 4;i++){
            double x1 = xCorners[i];
            double y1 = yCorners[i];
            double x2 = xCorners[(i + 1) % 4];
            double y2 = yCorners[(i + 1) % 4];

            Wall wall = new Wall(x1, y1, x2, y2, 8 , color);
            walls.add(wall);
            group.getChildren().add(wall);
        }
    }



    public void createPartical(double locationX, double locationY, double mass, double radius){      
        Color particleColor = getParticleColor();
        PhysicsCircle partical = new PhysicsCircle(locationX, locationY, radius, mass, particleColor);

        partical.setOnMousePressed(event -> {
            mouseX = event.getSceneX() - partical.getCenterX();
            mouseY = event.getSceneY() - partical.getCenterY();
            partical.setVelocity(0, 0);
        });

        partical.setOnMouseDragged(event -> {
            partical.setCenterX(event.getSceneX() - mouseX);
            partical.setCenterY(event.getSceneY() - mouseY);
        });

        partical.setVelocity(8,3);

        group.getChildren().add(partical);
        physics.addCircle(partical);
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