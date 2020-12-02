import model.Vec2Int;

public class DataAttack {

    int idEntity;

    MyEntity mMyEntity;
    Vec2Int position;


    public DataAttack(int idEntity)
    {
        this.idEntity = idEntity;
    }

    public int getIdEntity() {
        return idEntity;
    }

    public MyEntity getMyEntity() {
        return mMyEntity;
    }

    public Vec2Int getPosition() {
        return position;
    }

    public void setIdEntity(int idEntity) {
        this.idEntity = idEntity;
    }

    public void setMyEntity(MyEntity myEntity) {
        mMyEntity = myEntity;
    }

    public void setPosition(Vec2Int position) {
        this.position = position;
    }
}
