package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class Entity {
    private int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    private Integer playerId;
    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }
    private model.EntityType entityType;
    public model.EntityType getEntityType() { return entityType; }
    public void setEntityType(model.EntityType entityType) { this.entityType = entityType; }
    private model.Vec2Int position;
    public model.Vec2Int getPosition() { return position; }
    public void setPosition(model.Vec2Int position) { this.position = position; }
    private int health;
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    private boolean active;
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Entity() {}
    public Entity(int id, Integer playerId, model.EntityType entityType, model.Vec2Int position, int health, boolean active) {
        this.id = id;
        this.playerId = playerId;
        this.entityType = entityType;
        this.position = position;
        this.health = health;
        this.active = active;
    }
    public static Entity readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        Entity result = new Entity();
        result.id = inputByteBuffer.getInt();
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.playerId = inputByteBuffer.getInt();
        } else {
            result.playerId = null;
        }
        switch (inputByteBuffer.getInt()) {
        case 0:
            result.entityType = model.EntityType.WALL;
            break;
        case 1:
            result.entityType = model.EntityType.HOUSE;
            break;
        case 2:
            result.entityType = model.EntityType.BUILDER_BASE;
            break;
        case 3:
            result.entityType = model.EntityType.BUILDER_UNIT;
            break;
        case 4:
            result.entityType = model.EntityType.MELEE_BASE;
            break;
        case 5:
            result.entityType = model.EntityType.MELEE_UNIT;
            break;
        case 6:
            result.entityType = model.EntityType.RANGED_BASE;
            break;
        case 7:
            result.entityType = model.EntityType.RANGED_UNIT;
            break;
        case 8:
            result.entityType = model.EntityType.RESOURCE;
            break;
        case 9:
            result.entityType = model.EntityType.TURRET;
            break;
        default:
            throw new java.io.IOException("Unexpected tag value");
        }
        result.position = model.Vec2Int.readFrom(inputByteBuffer);
        result.health = inputByteBuffer.getInt();
        result.active = FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, id);
        if (playerId == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            StreamUtilBAD.writeInt(stream, playerId);
        }
        StreamUtilBAD.writeInt(stream, entityType.tag);
        position.writeTo(stream);
        StreamUtilBAD.writeInt(stream, health);
        StreamUtilBAD.writeBoolean(stream, active);
    }
}
