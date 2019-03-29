package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    Map map;
    Robot robot1;
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        AnchorPane root = loader.load();
        controller = (Controller) loader.getController();
        primaryStage.setTitle("Path finder");
        map = new Map(controller.getStackPane(),20);
        primaryStage.setScene(new Scene(root, 850, 700));
        primaryStage.show();
        setUpRobot();
        controller.setMap(map);
    }

    void setUpRobot() {
        robot1 = new Robot(map.getMap().length - 1, map.getMap().length - 1);
        map.addRobot(robot1);
        controller.addRobot(robot1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
