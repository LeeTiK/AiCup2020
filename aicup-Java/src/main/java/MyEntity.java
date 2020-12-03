import model.Entity;
import model.EntityType;
import model.Vec2Int;

public class MyEntity extends Entity {

    boolean update = true;

    EUnitState mEUnitState;

    int simulationHP;




    public MyEntity(Entity entity) {
        super(entity.getId(), entity.getPlayerId(), entity.getEntityType(), entity.getPosition(), entity.getHealth(), entity.isActive());

        init();
    }

    public MyEntity(int id, Integer playerId, EntityType entityType, Vec2Int position, int health, boolean active) {
        super(id, playerId, entityType, position, health, active);

        init();
    }

    public MyEntity() {
        init();
    }

    void  init(){
        mEUnitState = EUnitState.CREATE;
        simulationHP = getHealth();
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
        simulationHP = getHealth();
        setUpdate(true);
    }

    public int getSimulationHP() {
        return simulationHP;
    }

    public void attackHP(int attack) {
        this.simulationHP -= attack;
    }

    public void setEUnitState(EUnitState EUnitState) {
        mEUnitState = EUnitState;
    }
}
