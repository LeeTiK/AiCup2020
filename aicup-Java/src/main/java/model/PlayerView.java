package model;

import strategy.Final;
import util.FinalProtocol;
import util.StreamUtilBAD;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public class PlayerView {
    private int myId;
    public int getMyId() { return myId; }
    public void setMyId(int myId) { this.myId = myId; }
    private int mapSize;
    public int getMapSize() { return mapSize; }
    public void setMapSize(int mapSize) { this.mapSize = mapSize; }
    private boolean fogOfWar;
    public boolean isFogOfWar() { return fogOfWar; }
    public void setFogOfWar(boolean fogOfWar) { this.fogOfWar = fogOfWar; }
    private java.util.Map<model.EntityType, model.EntityProperties> entityProperties;
    public java.util.Map<model.EntityType, model.EntityProperties> getEntityProperties() { return entityProperties; }
    public void setEntityProperties(java.util.Map<model.EntityType, model.EntityProperties> entityProperties) { this.entityProperties = entityProperties; }
    private int maxTickCount;
    public int getMaxTickCount() { return maxTickCount; }
    public void setMaxTickCount(int maxTickCount) { this.maxTickCount = maxTickCount; }
    private int maxPathfindNodes;
    public int getMaxPathfindNodes() { return maxPathfindNodes; }
    public void setMaxPathfindNodes(int maxPathfindNodes) { this.maxPathfindNodes = maxPathfindNodes; }
    private int currentTick;
    public int getCurrentTick() { return currentTick; }
    public void setCurrentTick(int currentTick) { this.currentTick = currentTick; }
    private model.Player[] players;
    public model.Player[] getPlayers() { return players; }
    public void setPlayers(model.Player[] players) { this.players = players; }
    private model.Entity[] entities;
    public model.Entity[] getEntities() { return entities; }
    public void setEntities(model.Entity[] entities) { this.entities = entities; }
    public PlayerView() {}
    public PlayerView(int myId, int mapSize, boolean fogOfWar, java.util.Map<model.EntityType, model.EntityProperties> entityProperties, int maxTickCount, int maxPathfindNodes, int currentTick, model.Player[] players, model.Entity[] entities) {
        this.myId = myId;
        this.mapSize = mapSize;
        this.fogOfWar = fogOfWar;
        this.entityProperties = entityProperties;
        this.maxTickCount = maxTickCount;
        this.maxPathfindNodes = maxPathfindNodes;
        this.currentTick = currentTick;
        this.players = players;
        this.entities = entities;
    }
    public static PlayerView readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        PlayerView result = new PlayerView();
        result.myId = inputByteBuffer.getInt();
        result.mapSize = inputByteBuffer.getInt();
        result.fogOfWar = FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer);
        int entityPropertiesSize = inputByteBuffer.getInt();
        result.entityProperties = new java.util.HashMap<>(entityPropertiesSize);
        for (int i = 0; i < entityPropertiesSize; i++) {
            model.EntityType entityPropertiesKey;
            switch (inputByteBuffer.getInt()) {
            case 0:
                entityPropertiesKey = model.EntityType.WALL;
                break;
            case 1:
                entityPropertiesKey = model.EntityType.HOUSE;
                break;
            case 2:
                entityPropertiesKey = model.EntityType.BUILDER_BASE;
                break;
            case 3:
                entityPropertiesKey = model.EntityType.BUILDER_UNIT;
                break;
            case 4:
                entityPropertiesKey = model.EntityType.MELEE_BASE;
                break;
            case 5:
                entityPropertiesKey = model.EntityType.MELEE_UNIT;
                break;
            case 6:
                entityPropertiesKey = model.EntityType.RANGED_BASE;
                break;
            case 7:
                entityPropertiesKey = model.EntityType.RANGED_UNIT;
                break;
            case 8:
                entityPropertiesKey = model.EntityType.RESOURCE;
                break;
            case 9:
                entityPropertiesKey = model.EntityType.TURRET;
                break;
            default:
                throw new java.io.IOException("Unexpected tag value");
            }
            model.EntityProperties entityPropertiesValue;
            entityPropertiesValue = model.EntityProperties.readFrom(inputByteBuffer);
            result.entityProperties.put(entityPropertiesKey, entityPropertiesValue);
        }
        result.maxTickCount = inputByteBuffer.getInt();
        result.maxPathfindNodes = inputByteBuffer.getInt();
        result.currentTick = inputByteBuffer.getInt();
        result.players = new model.Player[inputByteBuffer.getInt()];
        System.out.println("players size: " + result.players.length);
        for (int i = 0; i < result.players.length; i++) {
            result.players[i] = model.Player.readFrom(inputByteBuffer);
        }
        result.entities = new model.Entity[inputByteBuffer.getInt()];
        System.out.println("entities size: " + result.entities.length);
        for (int i = 0; i < result.entities.length; i++) {
            result.entities[i] = model.Entity.readFrom(inputByteBuffer);
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, myId);
        StreamUtilBAD.writeInt(stream, mapSize);
        StreamUtilBAD.writeBoolean(stream, fogOfWar);
        StreamUtilBAD.writeInt(stream, entityProperties.size());
        for (java.util.Map.Entry<model.EntityType, model.EntityProperties> entityPropertiesEntry : entityProperties.entrySet()) {
            model.EntityType entityPropertiesKey = entityPropertiesEntry.getKey();
            model.EntityProperties entityPropertiesValue = entityPropertiesEntry.getValue();
            StreamUtilBAD.writeInt(stream, entityPropertiesKey.tag);
            entityPropertiesValue.writeTo(stream);
        }
        StreamUtilBAD.writeInt(stream, maxTickCount);
        StreamUtilBAD.writeInt(stream, maxPathfindNodes);
        StreamUtilBAD.writeInt(stream, currentTick);
        StreamUtilBAD.writeInt(stream, players.length);
        for (model.Player playersElement : players) {
            playersElement.writeTo(stream);
        }
        StreamUtilBAD.writeInt(stream, entities.length);
        for (model.Entity entitiesElement : entities) {
            entitiesElement.writeTo(stream);
        }
    }
}
