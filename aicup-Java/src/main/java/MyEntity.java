import model.Entity;
import model.EntityType;
import model.Vec2Int;

public class MyEntity extends Entity {

    boolean update = true;

    public MyEntity(Entity entity) {
        super(entity.getId(), entity.getPlayerId(), entity.getEntityType(), entity.getPosition(), entity.getHealth(), entity.isActive());
    }

    public MyEntity(int id, Integer playerId, EntityType entityType, Vec2Int position, int health, boolean active) {
        super(id, playerId, entityType, position, health, active);
    }

    public MyEntity() {
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    public void update(Entity entity) {
        setPosition(entity.getPosition());
        setHealth(entity.getHealth());
        setActive(entity.isActive());
        setUpdate(true);
    }
}