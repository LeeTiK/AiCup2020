import model.Entity;

public class MyUnit extends MyEntity {

    public MyUnit(){

    }

    public MyUnit(Entity entity) {
        super(entity.getId(),entity.getPlayerId(),entity.getEntityType(),entity.getPosition(),entity.getHealth(),entity.isActive());
    }
}
