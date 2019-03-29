package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class Controller {

    @FXML
    private Slider travelSpeed;

    @FXML
    private Slider searchSpeed;

    @FXML
    private StackPane stackPane;

    @FXML
    private ToggleGroup algorithm;

    @FXML
    private ToggleGroup click;

    Map map;
    private ArrayList<Robot> robots = new ArrayList<>();

    @FXML
    void initialize() {
        algorithm.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if ((((RadioButton) newValue).getText().contentEquals("A*"))) {
                for (Robot r : robots)
                    r.setUsedAlgorithm(Robot.Algorithm.AStar);
            } else {
                for (Robot r : robots)
                    r.setUsedAlgorithm(Robot.Algorithm.DIJKSTRA);
            }

        });
        click.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            String str = ((ToggleButton) newValue).getText();
            if (str.contentEquals("Destination")) {
                map.setMouseMode(Map.MouseMode.DESTINATION);
            } else if (str.contentEquals("Start")) {
                map.setMouseMode(Map.MouseMode.START);
            } else if (str.contentEquals("Obstacle")) {
                map.setMouseMode(Map.MouseMode.OBSTACLE);
            }

        });
        searchSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (Robot r : robots) r.setSearchSpeed(newValue.longValue());
        });
        travelSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            for (Robot r : robots) r.setTravelSpeed(newValue.longValue());
        });

    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void addRobot(Robot robot) {
        this.robots.add(robot);
    }

    @FXML
    void saveMap(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save map as :");
        fileChooser.getExtensionFilters().removeAll();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add((new FileChooser.ExtensionFilter("Map file (.map)", "*.map")));
        File f = fileChooser.showSaveDialog(stackPane.getParent().getScene().getWindow());
        if (f != null) {
            if (!f.getName().contains(".map"))
                f = new File(f.getAbsolutePath() + ".map");
            map.saveMap(f);
        }
    }

    @FXML
    void loadMap(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save map as :");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().removeAll();
        fileChooser.getExtensionFilters().add((new FileChooser.ExtensionFilter("Map file (.map)", "*.map")));
        File f = fileChooser.showOpenDialog(stackPane.getParent().getScene().getWindow());
        if (f != null)
            map.loadMap(f);
    }

    @FXML
    void newMap(ActionEvent event) {
        map.newMap();
    }

    @FXML
    void help(ActionEvent event) {
        InfoDialog.show("To draw your obstacle map select Obstacle in click action and then use :\n[Left click + drag] : to draw\n[Right click + drag] : to erase" +
                "\n\nYou can save your current map and open it later by clicking on map, save as/open.\n\nOnce the map is created,select the algorithm you want to use,then" +
                " click start in click action and select a point on the map to set the robot start point, next click on destination, and select a point you want to find a path to, to start the algorithm.");
    }

    @FXML
    void about(ActionEvent event) {
        InfoDialog.show("Developped by Walid BOUDEBOUDA\nwalid.boudebouda@gmail.com\nhttps://github.com/walid-git/");
    }

}
