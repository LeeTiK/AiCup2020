package model;

import pool.CacheVec2Int;
import util.StreamUtil;

public class Vec2Int {
    private int x;

    public Vec2Int(double v, double v1) {
        x = (int) v;
        y = (int) v1;
    }

    public int getX() { return x; }
   // public void setX(int x) { this.x = x; }
    private int y;
    public int getY() { return y; }
    //public void setY(int y) { this.y = y; }
    public Vec2Int() {}
    public Vec2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public static Vec2Int readFrom(java.io.InputStream stream) throws java.io.IOException {
        int x = StreamUtil.readInt(stream);
        int y = StreamUtil.readInt(stream);
        Vec2Int vec2Int = CacheVec2Int.getVec2Int(x,y);
        return vec2Int;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeInt(stream, x);
        StreamUtil.writeInt(stream, y);
    }

/*
    //0° is horizontal to the right. Counter-clockwise, radian!
    public static Vec2Int fromAngle(double alpha, double length) {
        return Vec2Int.createVector(Math.cos(alpha) * length, Math.sin(alpha) * length);

    }

    */

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public Vec2Int copy() {
        return Vec2Int.createVector(x, y);
    }

    public double distance(Vec2Int v) {
        return subtract(v).length();
    }

    public double length() {
        return (double) Math.sqrt(x * x + y * y);
    }

    public double squaredLength() {
        return dotProduct(this);
    }

    /*
    public Vec2Int normalize() {
        double l = length();
        return scalar(1 / l);
    }
*/
    public Vec2Int orthogonal() {
        return Vec2Int.createVector(-y, x);
    }

    public Vec2Int add(Vec2Int v) {
        return Vec2Int.createVector(x + v.x, y + v.y);
    }

    public Vec2Int subtract(Vec2Int v) {
        return Vec2Int.createVector(x - v.x, y - v.y);
    }

    /*
    public Vec2Int scalar(double a) {
        return Vec2Int.createVector(x * a, y * a);
    }

    public Vec2Int div(double con) {
        return Vec2Int.createVector(this.x / con, this.y / con);
    }
    */
    public double dotProduct(Vec2Int v) {
        return x * v.x + y * v.y;
    }

    public double angle() {
        double angle = (double) Math.atan2(y, x);
        // if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    public Vec2Int negate() {
        return Vec2Int.createVector(-x, -y);
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() == Vec2Int.class) {
            Vec2Int v = (Vec2Int) o;
            return x == v.x && y == v.y;
        }
        return false;
    }

    public Vec2Int add(int x1, int y1) {
        return Vec2Int.createVector(x + x1, y + y1);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Vec2Int subtract(int x1, int y1) {
        return createVector(x - x1, y- y1);
    }
    public Vec2Float getVec2Float(){
        return new Vec2Float(x,y);
    }

    static public Vec2Int createVector(int x, int y){
        return CacheVec2Int.getVec2Int(x,y);
    }

}
