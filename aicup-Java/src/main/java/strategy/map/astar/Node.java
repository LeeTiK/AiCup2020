package strategy.map.astar;

import model.Vec2Int;

public class Node implements Comparable<Node> {

    public Vec2Int coord; //
    public Node parent; //
    public int G; // G：
    public int H; // H：

    public Node(int x, int y) {
        this.coord = Vec2Int.createVector(x, y);
    }

    public Node(Vec2Int vec2Int) {
        this.coord = vec2Int.copy();
    }

    public Node(Vec2Int coord, Node parent, int g, int h) {
        this.coord = coord;
        this.parent = parent;
        G = g;
        H = h;
    }

    @Override
    public int compareTo(Node o) {
        if (o == null) return -1;
        if (G + H > o.G + o.H)
            return 1;
        else if (G + H < o.G + o.H) return -1;
        return 0;
    }
}