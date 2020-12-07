package strategy.map.astar;

import model.Vec2Int;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class AStar {
    public final static int BAR = 1;
    public final static int PATH = 2;
    public final static int DIRECT_VALUE = 10;

    Queue<Node> openList = new PriorityQueue<Node>();
    List<Node> closeList = new ArrayList<Node>();


    public Node start(MapInfo mapInfo) {
        if (mapInfo == null) return null;
        // clean
        openList.clear();
        closeList.clear();
        // 开始搜索
        openList.add(mapInfo.start);
        return moveNodes(mapInfo);
    }


    /**
     * 移动当前结点
     */
    private Node moveNodes(MapInfo mapInfo) {
        while (!openList.isEmpty()) {
            Node current = openList.poll();
            closeList.add(current);
            addNeighborNodeInOpen(mapInfo, current);
            if (isCoordInClose(mapInfo.end.coord)) {
                return mapInfo.end;
            }
        }
        return null;
    }

    /**
     * 在二维数组中绘制路径
     */
    private void drawPath(int[][] maps, Node end) {
        if (end == null || maps == null) return;
        System.out.println("Путь：" + end.G);
        while (end != null) {
            Vec2Int c = end.coord;
            maps[c.getY()][c.getX()] = PATH;
            end = end.parent;
        }
    }

    /**
     * 添加所有邻结点到open表
     */
    private void addNeighborNodeInOpen(MapInfo mapInfo, Node current) {
        int x = current.coord.getX();
        int y = current.coord.getY();
        // 左
        addNeighborNodeInOpen(mapInfo, current, x - 1, y, DIRECT_VALUE);
        // 上
        addNeighborNodeInOpen(mapInfo, current, x, y - 1, DIRECT_VALUE);
        // 右
        addNeighborNodeInOpen(mapInfo, current, x + 1, y, DIRECT_VALUE);
        // 下
        addNeighborNodeInOpen(mapInfo, current, x, y + 1, DIRECT_VALUE);
    }

    /**
     * 添加一个邻结点到open表
     */
    private void addNeighborNodeInOpen(MapInfo mapInfo, Node current, int x, int y, int value) {
        if (canAddNodeToOpen(mapInfo, x, y)) {
            Node end = mapInfo.end;
            Vec2Int coord = new Vec2Int(x, y);
            int G = current.G + value;
            Node child = findNodeInOpen(coord);
            if (child == null) {
                int H = calcH(end.coord, coord);
                if (isEndNode(end.coord, coord)) {
                    child = end;
                    child.parent = current;
                    child.G = G;
                    child.H = H;
                } else {
                    child = new Node(coord, current, G, H);
                }
                openList.add(child);
            } else if (child.G > G) {
                child.G = G;
                child.parent = current;
                openList.add(child);
            }
        }
    }

    /**
     * 从Open列表中查找结点
     */
    private Node findNodeInOpen(Vec2Int coord) {
        if (coord == null || openList.isEmpty()) return null;
        for (Node node : openList) {
            if (node.coord.equals(coord)) {
                return node;
            }
        }
        return null;
    }


    /**
     * 计算H的估值：“曼哈顿”法，坐标分别取差值相加
     */
    private int calcH(Vec2Int end, Vec2Int coord) {
        return Math.abs(end.getX() - coord.getX())
                + Math.abs(end.getY() - coord.getY());
    }

    /**
     * 判断结点是否是最终结点
     */
    private boolean isEndNode(Vec2Int end, Vec2Int coord) {
        return coord != null && end.equals(coord);
    }

    /**
     * 判断结点能否放入Open列表
     */
    private boolean canAddNodeToOpen(MapInfo mapInfo, int x, int y) {

        if (x < 0 || x >= mapInfo.width || y < 0 || y >= mapInfo.hight) return false;

        if (mapInfo.maps[y][x] == BAR) return false;

        if (isCoordInClose(x, y)) return false;

        return true;
    }

    /**
     * 判断坐标是否在close表中
     */
    private boolean isCoordInClose(Vec2Int coord) {
        return coord != null && isCoordInClose(coord.getX(), coord.getY());
    }

    /**
     * 判断坐标是否在close表中
     */
    private boolean isCoordInClose(int x, int y) {
        if (closeList.isEmpty()) return false;
        for (Node node : closeList) {
            if (node.coord.getX() == x && node.coord.getY() == y) {
                return true;
            }
        }
        return false;
    }
}
