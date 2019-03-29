package sample;

import javafx.application.Platform;
import sample.Observer.Observable;
import sample.Observer.Observer;

import java.util.ArrayList;
import java.util.Arrays;

public class Robot implements Observable {
    public enum Algorithm {DIJKSTRA, AStar}

    private volatile int x;
    private volatile int y;
    private Map.Cell[][] obstaclesMap;
    private Map map;
    private Algorithm usedAlgorithm = Algorithm.DIJKSTRA;
    private int mapDimens;
    int[][] c;
    int[] p;
    private long travelSpeed = 350, searchSpeed = 485;

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(this);
        }
    }

    ArrayList<Observer> observers = new ArrayList<Observer>();

    public Robot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        notifyObservers();
    }

    public void setPosition(int node) {
        this.x = node % obstaclesMap.length;
        this.y = node / obstaclesMap.length;
        notifyObservers();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        notifyObservers();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        notifyObservers();
    }

    public int getNode() {
        return y * obstaclesMap.length + x;
    }

    public void setMap(Map myMap) {
        this.map = myMap;
        this.obstaclesMap = myMap.getMap();
        this.mapDimens = myMap.getDimension();
        mapDimens = this.obstaclesMap.length;
        c = new int[mapDimens * mapDimens][mapDimens * mapDimens];
        for (int i = 0; i < mapDimens * mapDimens; i++)
            for (int j = 0; j < mapDimens * mapDimens; j++)
                c[i][j] = Integer.MAX_VALUE;

        for (int i = 0; i < mapDimens * mapDimens; i++) {
            if (obstaclesMap[i / mapDimens][i % mapDimens] == Map.Cell.OBSTACLE)
                continue;
            for (int j = 0; j < mapDimens * mapDimens; j++) {
                if (obstaclesMap[j / mapDimens][j % mapDimens] == Map.Cell.OBSTACLE)
                    continue;
                else if (j == i)
                    c[i][j] = 0;
//                else if (j == i + dimension || j == i - dimension || (j == i + 1 && !(j % dimension == 0)) || (j == i - 1 && !(j % dimension == dimension - 1)) || (j == i - dimension + 1 && !(j % dimension == 0)) || (j == i - dimension - 1 && !(j % dimension == dimension - 1)) || (j == i + dimension +1 && !(j % dimension == 0 )) || (j == i + dimension - 1 && !(j % dimension == dimension - 1)))
                else if (j == i + mapDimens || j == i - mapDimens || (j == i + 1 && !(j % mapDimens == 0)) || (j == i - 1 && !(j % mapDimens == mapDimens - 1)))
                    c[i][j] = 1;
            }
        }
    }

    private void traversePath(int des) {

        if (p[des] == -1) {
            System.out.println(" -> " + des);
            setPosition(des);
        } else {
            traversePath(p[des]);
            setPosition(des);
            System.out.println(" -> " + des);
        }
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void traversePath(int des, int count) {

        if (p[des] == -1) {
            System.out.println("path length " + count);
//            System.out.println(" -> " + des);
            setPosition(des);
        } else {
            traversePath(p[des], count + 1);
            setPosition(des);
//            System.out.println(" -> " + des);
        }
        try {
            Thread.sleep(450 - travelSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void path(int des) {
        if (p[des] == -1)
            System.out.print(" -> " + des);
        else {
            path(p[des]);
            System.out.print(" -> " + des);
        }
    }

    void dijkstra(int s, int des) {
        int visitedNodes = 0;
        map.clearVisitedNodes();
        map.setDestination(des);
        map.setStart(s);
        int[] d = Arrays.copyOf(c[s], c[s].length);// distance from source node to any node
        p = new int[d.length];  //last node to go through before reaching any node
        boolean[] add = new boolean[d.length];//mark visited nodes
        Arrays.fill(p, -1);
        for (int i = 0; i < d.length; i++) {
            int t, j, k = 0, min = Integer.MAX_VALUE;

            for (j = 0; j < d.length; j++) {
                if (d[j] < min && !add[j]) {
                    min = d[j];
                    k = j;
                }
            }
            if (k == des ||  min == Integer.MAX_VALUE)
                break;
//            System.out.println("minimum found " + min + " at " + k);
            for (t = 0; t < d.length; t++)
                if (d[t] > d[k] + c[k][t] && d[k] + c[k][t] > 0) {
                    d[t] = d[k] + c[k][t];
                    p[t] = k;
//                    System.out.println("updating value " + t + " new value " + d[t]);
                }
            add[k] = true;
            visitedNodes++;
            map.setVisitedNode(k);
            try {
                Thread.sleep(500 - searchSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
//        System.out.println("\dimension\nshortest path from " + s + " to " + des + " is " + d[des] + " through ");
//        System.out.print(s);
//        path(des);
        System.out.println(" dijkstra visited nodes :" + visitedNodes);
        if (d[des] != Integer.MAX_VALUE)
            new Thread(() -> traversePath(des, 1)).start();
        else
            Platform.runLater(() -> InfoDialog.show("There is no possible path"));

    }

    void aStar(int s, int des) {
        int visitedNodes = 0;
        map.clearVisitedNodes();
        map.setDestination(des);
        map.setStart(s);
        int[] d = Arrays.copyOf(c[s], c[s].length);// distance from source node to any node
        p = new int[d.length];  //last node to go through before reaching any node
        boolean[] add = new boolean[d.length];//mark visited nodes
        double[] h = new double[d.length]; //heuristic function, straight line distance from any node to destination
        for (int i = 0; i < d.length; i++) {
            p[i] = -1;
            int dx = Math.abs((i % map.getDimension()) - (des % map.getDimension()));
            int dy = Math.abs((i / map.getDimension()) - (des / map.getDimension()));
            h[i] = Math.sqrt(dx * dx + dy * dy);
        }

        for (int i = 0; i < d.length; i++) {
            int t, j, k = 0;
            double min = Double.MAX_VALUE;

                for (j = d.length - 1; j >= 0; j--) {
                    if (d[j] + h[j] < min && !add[j]) {
                        min = d[j] + h[j];
                        k = j;
                    }
                }

//            System.out.println("minimum found " + min + " at " + k);
            for (t = 0; t < d.length; t++)
                if (d[t] > d[k] + c[k][t] && d[k] + c[k][t] > 0) {
                    d[t] = d[k] + c[k][t];
                    p[t] = k;
//                    System.out.println("updating value " + t + " new value " + d[t]);
                }
            add[k] = true;
            visitedNodes++;
            map.setVisitedNode(k);
            try {
                Thread.sleep(500 - searchSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (k == des)
                break;
        }
//        path(des);
        System.out.println("A* visited nodes :" + visitedNodes);
        if (d[des] != Integer.MAX_VALUE)
            new Thread(() -> traversePath(des, 1)).start();
        else
            Platform.runLater(() -> InfoDialog.show("There is no possible path"));


    }

    public void moveTo(int dest) {
        if (usedAlgorithm == Algorithm.DIJKSTRA)
            this.dijkstra(getNode(), dest);
        else if (usedAlgorithm == Algorithm.AStar) {
            this.aStar(getNode(),dest);
        }
    }

    public void setUsedAlgorithm(Algorithm usedAlgorithm) {
        this.usedAlgorithm = usedAlgorithm;
    }

    public void setSearchSpeed(long searchSpeed) {
        this.searchSpeed = searchSpeed;
    }

    public void setTravelSpeed(long travelSpeed) {
        this.travelSpeed = travelSpeed;
    }
}
