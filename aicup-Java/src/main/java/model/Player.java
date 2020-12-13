package model;

import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class Player {
    private int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    private int score;
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    private int resource;
    public int getResource() { return resource; }
    public void setResource(int resource) { this.resource = resource; }
    public Player() {}
    public Player(int id, int score, int resource) {
        this.id = id;
        this.score = score;
        this.resource = resource;
    }
    public static Player readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        Player result = new Player();
        result.id = inputByteBuffer.getInt();
        result.score = inputByteBuffer.getInt();
        result.resource = inputByteBuffer.getInt();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, id);
        StreamUtilBAD.writeInt(stream, score);
        StreamUtilBAD.writeInt(stream, resource);
    }
}
