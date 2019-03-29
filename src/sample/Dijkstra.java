package sample;

public class Dijkstra {

    // adjecency matrix
    public static final int SIZE = 5;
    static int[][] c = {{0, 10, Integer.MAX_VALUE, 30, 100},
            {Integer.MAX_VALUE, 0, 50, Integer.MAX_VALUE, Integer.MAX_VALUE},
            {Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 10},
            {Integer.MAX_VALUE, Integer.MAX_VALUE, 20, 0, 60},
            {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0}};
    // distance between source and all other nodes
    static int[] d = new int[5];
    // used to store intermediate nodes on the shortest path
    // ie : to get to some node dimension, we need to get through p[dimension]
    static int[] p = new int[5];
    // to store visited nodes
    static boolean[] add = new boolean[5];

    //calculate shortest path to all destinations
    static void path(int des) {
        if (p[des] == -1)
            System.out.print(" -> " + des);
//            cout<<"-> "<<des;
        else {
            path(p[des]);
            System.out.print(" -> " + des);
//            cout<<" -> "<<des;
        }
    }

    static void dijkstra(int s) {
        int i = 0;
        for (i = 0; i < SIZE; i++) {
            d[i] = c[s][i];
            p[i] = -1;
            System.out.print(d[i] + " through " + p[i] + " ,");
//            cout<<d[i]<<" through "<<p[i]<<" ,";
        }
//        cout<<endl;
        System.out.println();

        for (i = 0; i < SIZE; i++) {
            int t, j, k = 0, min = Integer.MAX_VALUE;

            for (j = 0; j < SIZE; j++) {
                if (d[j] < min && !add[j]) {
                    min = d[j];
                    k = j;
                }
            }
            System.out.println("minimum found " + min + " at " + k);
//            cout<<"minimum found "<< min<<" at "<<k<<endl;
            for (t = 0; t < SIZE; t++)
                if (d[t] > d[k] + c[k][t] && d[k] + c[k][t] > 0) {
//                    cout<<"Updating value "<<t<<endl;
                    d[t] = d[k] + c[k][t];
                    p[t] = k;
                    System.out.println("updating value " + t + " new value " + d[t]);
                }
            add[k] = true;
        }
    }
    //print the path to get to some node

    // calculate shortest path to single destination
    static void dijkstra(int s, int des) {
        int i = 0;
        for (i = 0; i < SIZE; i++) {
            d[i] = c[s][i];
            p[i] = -1;
            System.out.print(d[i] + " through " + p[i] + " ,");
//            cout<<d[i]<<" through "<<p[i]<<" ,";
        }
//        cout<<endl;
        System.out.println();
        for (i = 0; i < SIZE; i++) {
            int t, k = 0, min = Integer.MAX_VALUE;
            int j;
            for (j = 0; j < SIZE; j++) {
                if (d[j] < min && !add[j]) {
                    min = d[j];
                    k = j;
                }
            }
            System.out.println("minimum found " + min + " at " + k);
//            cout<<"minimum found "<< min<<" at "<<k<<endl;
            for (t = 0; t < SIZE; t++)
                if (d[t] > d[k] + c[k][t] && d[k] + c[k][t] > 0) {
//                    cout<<"Updating value "<<t<<endl;
                    d[t] = d[k] + c[k][t];
                    p[t] = k;
                    System.out.println("updating value " + t + " new value " + d[t]);
                }
            add[k] = true;
            if (k == des)
                break;
        }
//        cout<<"shortest path from "<<s<<" to "<<des<<" is "<<d[des]<< " through "<<endl;
        System.out.println("\n\nshortest path from " + s + " to " + des + " is " + d[des] + " through ");
        System.out.print(s);
        path(des);
//        cout<<endl;
    }

    public static void main(String[] args) {
        dijkstra(0, 4);
    }


}
