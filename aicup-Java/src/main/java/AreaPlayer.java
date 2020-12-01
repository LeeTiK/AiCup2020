import model.Vec2Int;

public class AreaPlayer {
    Vec2Int position;
    int width, height;

    public AreaPlayer(Vec2Int position, int width, int height)
    {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    Vec2Int getCenter(){
        return position.add(width/2,height/2);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
