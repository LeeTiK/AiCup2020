package strategy.map.astar;

import model.EntityType;
import model.Vec2Int;

/**
 * Node Class
 *
 * @author Marcelo Surriabre
 * @version 2.0, 2018-02-23
 */
public class Node {

    private int g;
    private int f;
    private int h;
    private int h2;
    private int row;
    private int col;
    private boolean isBlock;
    private boolean isBlockFirst;
    private boolean isBlockAttackUnit;
    private Node parent;
    private Node child;
    EntityType entityType;

    public Node(int row, int col) {
        super();
        this.row = row;
        this.col = col;
    }

    public Node(Vec2Int vec2Int) {
        super();
        this.row = vec2Int.getX();
        this.col = vec2Int.getY();
    }

    public void calculateHeuristic(Node finalNode) {
        int x  =finalNode.getRow() - getRow();
        int y  =finalNode.getCol() - getCol();
        this.h = (int) Math.sqrt(x*x+y*y);

    //    this.h = Math.abs(finalNode.getRow() - getRow()) + Math.abs(finalNode.getCol() - getCol());
    }

    public void setNodeData(Node currentNode, int cost, EntityType entityType) {
        int gCost = currentNode.getG() + cost;
        if (entityType==EntityType.BUILDER_UNIT && this.entityType == EntityType.BUILDER_UNIT){

        }
        else {
            gCost+=getH2();
        }
        setParent(currentNode);
        setG(gCost);
        calculateFinalCost();
    }

    public boolean checkBetterPath(Node currentNode, int cost, EntityType entityType) {
        int gCost = currentNode.getG() + cost + getH2();
        if (gCost < getG()) {
            setNodeData(currentNode, cost,entityType);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getH();
        setF(finalCost);
    }

    @Override
    public boolean equals(Object arg0) {
        Node other = (Node) arg0;
        return this.getRow() == other.getRow() && this.getCol() == other.getCol();
    }

    @Override
    public String toString() {
        return "Node [row=" + row + ", col=" + col + "]";
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isBlock(EntityType entityType) {
        if (entityType==EntityType.BUILDER_UNIT && this.entityType==EntityType.RESOURCE) return true;
        return isBlock;
    }

    public void setBlock(boolean isBlock) {

        if (isBlock==false){
            h2 = 0;
            isBlockFirst=false;
        }

        this.isBlock = isBlock;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Vec2Int getVec2Int(){
        return Vec2Int.createVector(row,col);
    }

    public void setH2(int h2) {
        this.h2 = h2;
    }

    public int getH2() {
        return h2;
    }

    public void setBlockAttackUnit(boolean blockAttackUnit) {
        isBlockAttackUnit = blockAttackUnit;
    }

    public boolean isBlockAttackUnit() {
        return isBlockAttackUnit;
    }

    public void setBlockFirst(boolean blockFirst) {
        isBlockFirst = blockFirst;
    }

    public boolean isBlockFirst() {
        return isBlockFirst;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    public void setType(EntityType entityType) {
        this.entityType = entityType;
    }
}
