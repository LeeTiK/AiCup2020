package strategy.map.astar;

import model.EntityType;
import model.Vec2Int;
import strategy.Final;
import strategy.FinalConstant;
import strategy.GlobalMap;
import strategy.MyEntity;
import strategy.map.potfield.MapPotField;

import java.util.*;

/**
 * A Star Algorithm
 *
 * @author Marcelo Surriabre
 * @version 2.1, 2017-02-23
 */
public class AStar {
    private static int DEFAULT_HV_COST = 1; // Horizontal - Vertical Cost
    private static int DEFAULT_HV_COST_RESOURSCE_ATTACK_UNIT = 6; //
    private static int DEFAULT_HV_COST_RESOURSCE_BUILD_UNIT = 30; //
    private static int DEFAULT_HV_COST_BUILD_UNIT = 3; //
    private static int DEFAULT_HV_COST_ATTACK_UNIT = 3; //
    private int hvCost;
    private int diagonalCost;
    private Node[][] searchArea;
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;
    private Node initialNode;
    private Node finalNode;

    EntityType mEntityTypeFindPath;

    List<Node> path = new ArrayList<Node>();

    public AStar(int size)
    {
        this.hvCost = DEFAULT_HV_COST;
        this.searchArea = new Node[size][size];

        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                this.searchArea[i][j] = new Node(i, j);;
            }
        }

        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        //  setNodes();
        this.closedSet = new HashSet<>();
    }

    public void initSearch(Vec2Int initialNode, Vec2Int finalNode){
        setInitialNode(new Node(initialNode));
        setFinalNode(new Node(finalNode));
        setNodes();
        openList.clear();
        closedSet.clear();
        path.clear();
    }

    public void updateMap(GlobalMap globalMap, MapPotField mapPotField){
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                this.searchArea[i][j].setBlock(false);
            }
        }

        for (int i = 0; i < globalMap.getMap().length; i++) {
            for (int j = 0; j < globalMap.getMap()[0].length; j++) {

                Vec2Int vec2Int = Vec2Int.createVector(i,j);

                MyEntity entity = globalMap.getMap()[vec2Int.getX()][vec2Int.getY()];

                this.searchArea[i][j].setType(entity.getEntityType());

                if (entity.getEntityType()== EntityType.RESOURCE)
                {
                    this.searchArea[i][j].setH2(DEFAULT_HV_COST_RESOURSCE_ATTACK_UNIT);
                }

                if (
                        entity.getEntityType()== EntityType.MELEE_BASE ||
                                entity.getEntityType()== EntityType.RANGED_BASE ||
                                entity.getEntityType()== EntityType.BUILDER_BASE ||
                                entity.getEntityType()== EntityType.TURRET ||
                                entity.getEntityType()== EntityType.HOUSE ||
                                entity.getEntityType()== EntityType.WALL
                ) {
                    this.searchArea[i][j].setBlock(true);
                }

                if (entity.getEntityType()== EntityType.BUILDER_UNIT &&
                        entity.getPlayerId()==FinalConstant.getMyID()){
                 /*   if (Final.A_STAR_BLOCK_ALL_BUILD_UNIT || globalMap.getSpecialCheckBuilderTask(vec2Int))
                    {
                        this.searchArea[i][j].setBlock(true);
                    }*/
                    this.searchArea[i][j].setH2(DEFAULT_HV_COST_BUILD_UNIT);
                }

                if (entity.getEntityType()== EntityType.RANGED_UNIT||
                        entity.getEntityType()== EntityType.MELEE_UNIT
                ){
                    if (entity.getPlayerId()==FinalConstant.getMyID())
                    {
                        if (mapPotField.getMapPotField()[vec2Int.getX()][vec2Int.getY()].getSumDanger() +
                                mapPotField.getMapPotField()[vec2Int.getX()][vec2Int.getY()].getSumDangerContour()
                                >0 ) {
                            this.searchArea[i][j].setBlock(true);
                        }
                        else {
                            this.searchArea[i][j].setH2(DEFAULT_HV_COST_ATTACK_UNIT);
                        }
                    }
                }
            }
        }
    }

    private void setNodes() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
               // Node node = new Node(i, j);
                this.searchArea[i][j].calculateHeuristic(getFinalNode());
            }
        }
    }

    public void setBlocks(int[][] blocksArray) {
        for (int i = 0; i < blocksArray.length; i++) {
            int row = blocksArray[i][0];
            int col = blocksArray[i][1];
            setBlock(row, col);
        }
    }

    public List<Node> findPath(EntityType entityType) {
        if (this.searchArea[finalNode.getVec2Int().getX()][finalNode.getVec2Int().getY()].isBlock(mEntityTypeFindPath)) return new ArrayList<Node>();
        this.mEntityTypeFindPath = entityType;

        openList.add(initialNode);
        int k = 0;
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode) || k>700) {
                return getPath(currentNode);
            } else {
                addAdjacentNodes(currentNode, k);
            }
            k++;
        }
        return new ArrayList<Node>();
    }

    private List<Node> getPath(Node currentNode) {
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            parent.setChild(currentNode);
            currentNode = parent;
        }
        return path;
    }

    private void addAdjacentNodes(Node currentNode, int k) {
        addAdjacentUpperRow(currentNode,k);
        addAdjacentMiddleRow(currentNode,k);
        addAdjacentLowerRow(currentNode,k);
    }

    private void addAdjacentLowerRow(Node currentNode,int k) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
          /*  if (col - 1 >= 0) {
                checkNode(currentNode, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }*/
            checkNode(currentNode, col, lowerRow, getHvCost(),k);
        }
    }

    private void addAdjacentMiddleRow(Node currentNode, int k) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= 0) {
            checkNode(currentNode, col - 1, middleRow, getHvCost(),k);
        }
        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, getHvCost(),k);
        }
    }

    private void addAdjacentUpperRow(Node currentNode, int k) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= 0) {
         /*   if (col - 1 >= 0) {
                checkNode(currentNode, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }*/
            checkNode(currentNode, col, upperRow, getHvCost(),k);
        }
    }

    private void checkNode(Node currentNode, int col, int row, int cost, int k) {
        Node adjacentNode = getSearchArea()[row][col];
        if (!Final.A_STAR_CHECK_FIRST_NODE_BLOCK || (k>1 || (k<=1 && !adjacentNode.isBlockFirst()))) {
            if (!adjacentNode.isBlock(mEntityTypeFindPath) && !getClosedSet().contains(adjacentNode)) {
                if (!getOpenList().contains(adjacentNode)) {
                    adjacentNode.setNodeData(currentNode, cost,mEntityTypeFindPath);
                    getOpenList().add(adjacentNode);
                } else {
                    boolean changed = adjacentNode.checkBetterPath(currentNode, cost,mEntityTypeFindPath);
                    if (changed) {
                        // Remove and Add the changed node, so that the PriorityQueue can sort again its
                        // contents with the modified "finalCost" value of the modified node
                        getOpenList().remove(adjacentNode);
                        getOpenList().add(adjacentNode);
                    }
                }
            }
        }
    }

    private boolean isFinalNode(Node currentNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.size() == 0;
    }

    private void setBlock(int row, int col) {
        this.searchArea[row][col].setBlock(true);
    }

    public Node getInitialNode() {
        return initialNode;
    }

    public void setInitialNode(Node initialNode) {
        this.initialNode = initialNode;
    }

    public Node getFinalNode() {
        return finalNode;
    }

    public void setFinalNode(Node finalNode) {
        this.finalNode = finalNode;
    }

    public Node[][] getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(Node[][] searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<Node> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    public Set<Node> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    public void addNewBlock(Vec2Int vec2Int)
    {
        this.searchArea[vec2Int.getX()][vec2Int.getY()].setBlockFirst(true);
    }
}

