package Graph;

import java.util.ArrayList;

public class Node{
    int val;
    int in;
    int out;
    ArrayList<Node> nexts;
    ArrayList<Edge> edges;

    public Node(int val, int in, int out) {
        this.val = val;
        this.in = in;
        this.out = out;
    }
}
