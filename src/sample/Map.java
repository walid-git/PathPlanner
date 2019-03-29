package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import sample.Observer.Observable;
import sample.Observer.Observer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Map extends Canvas implements Observer {

    public enum Cell {EMPTY, OBSTACLE, VISITED, START, DESTINATION}

    public enum MouseMode {OBSTACLE, START, DESTINATION}

    private double rectWidth;
    private double rectHeight;
    private final int dimension;
    private Cell[][] map;
    private ArrayList<Robot> robots = new ArrayList<>();
    Image robotImage;

    public Map(StackPane stackPane,int dimension) {
        this.dimension = dimension;
        stackPane.getChildren().add(this);
        widthProperty().bind(stackPane.widthProperty());
        heightProperty().bind(stackPane.heightProperty());
        map = new Cell[dimension][dimension];
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
        setFocusTraversable(true);
        setMouseMode(MouseMode.DESTINATION);
        newMap();
        robotImage = new Image(this.getClass().getClassLoader().getResourceAsStream("robot.png"));
    }
    public void newMap() {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                map[i][j] = Cell.EMPTY;
        for(Robot r : robots)
            r.setMap(this);
        Platform.runLater(this::draw);
    }

    public void setDestination(int node) {
        map[node / dimension][node % dimension] = Cell.DESTINATION;
    }

    public void setStart(int node) {
        map[node / dimension][node % dimension] = Cell.START;
    }

    public void loadMap(File f) {
        if (f.exists()) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(f);
                for (int i = 0; i < dimension; i++)
                    for (int j = 0; j < dimension; j++)
                        map[i][j] = is.read() == 1 ? Cell.OBSTACLE : Cell.EMPTY;
                System.out.println("map loaded !");
                for(Robot r : robots)
                    r.setMap(this);
                Platform.runLater(() -> draw());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("file does not exist");
        }
    }

    public void saveMap(File f) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(f);
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++)
                    os.write(map[i][j] == Cell.OBSTACLE ? 1 : 0);
            os.flush();
            System.out.println("map saved !");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        double w = getWidth();
        double h = getHeight();
        rectHeight = h / dimension;
        rectWidth = w / dimension;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.ALICEBLUE);
        gc.fillRect(0, 0, w, h);
        gc.setFill(Color.AQUA);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
//                System.out.println("Drawing node ::: "+map[i][j]);
                if (map[i][j] == Cell.OBSTACLE)
                    gc.fillRect(j * rectWidth, i * rectHeight, rectWidth, rectHeight);
                else if (map[i][j] == Cell.VISITED) {
                    gc.setFill(Color.RED);
                    gc.fillRect(j * rectWidth, i * rectHeight, rectWidth, rectHeight);
                    gc.setFill(Color.AQUA);
                } else if (map[i][j] == Cell.DESTINATION) {
                    gc.setFill(Color.YELLOW);
                    gc.fillRect(j * rectWidth, i * rectHeight, rectWidth, rectHeight);
                    gc.setFill(Color.AQUA);
                } else if (map[i][j] == Cell.START) {
                    gc.setFill(Color.GREENYELLOW);
                    gc.fillRect(j * rectWidth, i * rectHeight, rectWidth, rectHeight);
                    gc.setFill(Color.AQUA);
                }


            }
        }

        gc.setFill(Color.BLACK);
        for (Robot r : robots)
            gc.drawImage(robotImage, r.getX() * rectWidth, r.getY() * rectHeight, rectWidth, rectHeight);
        //            gc.fillRect(r.getX() * rectWidth, r.getY() * rectHeight, rectWidth, rectHeight);


    }

    public int getDimension() {
        return dimension;
    }

    public void setVisitedNode(int node) {
        if (map[node / dimension][node % dimension] == Cell.EMPTY) {
            map[node / dimension][node % dimension] = Cell.VISITED;
            Platform.runLater(() -> draw());
        }
    }

    public void clearVisitedNodes() {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                if (map[i][j] == Cell.VISITED || map[i][j] == Cell.DESTINATION || map[i][j] == Cell.START)
                    map[i][j] = Cell.EMPTY;
        Platform.runLater(() -> draw());
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public Cell[][] getMap() {
        return map;
    }

    public void addRobot(Robot robot) {
        robots.add(robot);
        robot.setMap(this);
        robot.addObserver(this);
        Platform.runLater(() -> draw());
    }

    public int getNode(double x, double y) {
        return dimension * (int) (y / rectHeight) + (int) (x / rectWidth);
    }

    @Override
    public void update(Observable o) {
        Platform.runLater(() -> draw());
    }

    public void setMouseMode(MouseMode mode) {
        if (mode == MouseMode.DESTINATION) {
            for(Robot r : robots)
                r.setMap(this);
            setOnMouseDragged(null);
            setOnMouseClicked(event -> {
                Robot robot = robots.get(0);
                synchronized (robot) {
                    int dest = getNode(event.getX(), event.getY());
//                new Thread(() -> robot.aStar(robot.getNode(), dest)).start();
                    if (map[dest / dimension][dest % dimension] != Cell.OBSTACLE)
                        for(Robot r : robots)
                        new Thread(() -> r.moveTo(dest)).start();
                }
            });
        } else if (mode == MouseMode.START) {
            setOnMouseClicked(event -> {
                int node = getNode(event.getX(), event.getY());
                if (map[node / dimension][node % dimension] != Cell.OBSTACLE) {
                    robots.get(0).setPosition(node);
                    clearVisitedNodes();
                }
            });
            setOnMouseDragged(null);
        } else if (mode == MouseMode.OBSTACLE) {
            clearVisitedNodes();
            setOnMouseDragged(event -> {
                int y = (int) (event.getY() / rectHeight);
                int x = (int) (event.getX() / rectWidth);
                if (event.getButton() == MouseButton.PRIMARY && map[y][x]==Cell.EMPTY) {
                    map[y][x] = Cell.OBSTACLE;
                } else if (event.getButton() == MouseButton.SECONDARY && map[y][x]==Cell.OBSTACLE)
                    map[y][x] = Cell.EMPTY;
                draw();
            });
            setOnMouseClicked(event -> {
                int y = (int) (event.getY() / rectHeight);
                int x = (int) (event.getX() / rectWidth);
                if (event.getButton() == MouseButton.PRIMARY && map[y][x]==Cell.EMPTY) {
                    map[y][x] = Cell.OBSTACLE;
                    System.out.println(event.getX());
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    map[y][x] = Cell.EMPTY;
                }
                draw();
            });
        }
    }
}
