import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.ArrayList;


public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double mouseX;
    private double mouseY;

    private ArrayList<PhysicsCircle> circles = new ArrayList<>();
    private ArrayList<Wall> walls = new ArrayList<>();
    private Group group = new Group();

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.BLACK);

        drawSquare(100, 50, 500, Color.GREEN);

        double startX = 300;
        double centerY = 500;
        double spacing = 70;
        double mass = 5;
        Random random = new Random();
        Color[] colors = { Color.DODGERBLUE, Color.CORNFLOWERBLUE, Color.STEELBLUE, Color.SKYBLUE, Color.LIGHTSKYBLUE };

        for (int index = 0; index < 2; index++) {

            double x = startX + index * spacing;
            double y = centerY;
            PhysicsCircle circle = new PhysicsCircle(x, y, 5, mass, colors[index % colors.length]);

            circle.setOnMousePressed(event -> {
                mouseX = event.getSceneX() - circle.getCenterX();
                mouseY = event.getSceneY() - circle.getCenterY();
                circle.setVelocity(0, 0);

            });

            circle.setOnMouseDragged(event -> {
                circle.setCenterX(event.getSceneX() - mouseX);
                circle.setCenterY(event.getSceneY() - mouseY);
            });

            circle.setVelocity((random.nextDouble() ),(random.nextDouble()));

            circles.add(circle);
            group.getChildren().add(circle);
        }

        Physics physics = new Physics(circles, walls);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                physics.applyVelocity();
                //physics.checkCircleCollisions();
                
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
}