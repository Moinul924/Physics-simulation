import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.Random;
import java.util.ArrayList;


public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double mouseX;
    private double mouseY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group group = new Group();
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.WHITE);
        ArrayList<PhysicsCircle> circles = new ArrayList<>();
        ArrayList<Wall> walls = new ArrayList<>();

        Wall line = new Wall(50,500,700,500);
        Wall line2 = new Wall(700,500,700,50);
        Wall Line3 = new Wall(700,50,50,50);
        Wall line4 = new Wall(50,50,50,500);
        Wall line5 = new Wall(100,120,300,200);
        walls.add(line);
        walls.add(line2);
        walls.add(Line3);
        walls.add(line4);
        walls.add(line5);
        group.getChildren().add(line);
        group.getChildren().add(line2);
        group.getChildren().add(Line3);
        group.getChildren().add(line4);
        group.getChildren().add(line5);

        

        double startX = 60;
        double centerY = 120;
        double spacing = 70;
        double mass = 5;
        Random random = new Random();
        Color[] colors = { Color.DODGERBLUE, Color.CORNFLOWERBLUE, Color.STEELBLUE, Color.SKYBLUE, Color.LIGHTSKYBLUE };

        for (int index = 0; index < 10; index++) {

            double x = startX + index * spacing;
            double y = centerY;
            PhysicsCircle circle = new PhysicsCircle(100, 100, 5, mass, Color.DODGERBLUE);

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
    
}
