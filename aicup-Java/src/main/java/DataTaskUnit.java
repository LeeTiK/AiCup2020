import model.Entity;
import model.EntityType;

public class DataTaskUnit {

    EUnitState mUnitState;

    EntityType mEntityType;
    Entity mEntity;


    public DataTaskUnit(EUnitState unitState)
    {
        mUnitState = unitState;
    }

    public Entity getEntity() {
        return mEntity;
    }

    public EntityType getEntityType() {
        return mEntityType;
    }

    public EUnitState getUnitState() {
        return mUnitState;
    }

    public void setEntity(Entity entity) {
        mEntity = entity;
    }

    public void setEntityType(EntityType entityType) {
        mEntityType = entityType;
    }

    public void clear() {
        mUnitState = EUnitState.EMPTY;
        mEntityType = null;
        mEntity = null;
    }
}
