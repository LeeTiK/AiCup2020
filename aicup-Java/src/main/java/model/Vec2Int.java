package model;

import util.StreamUtil;

public class Vec2Int {
    private int x;

    public Vec2Int(double v, double v1) {
        x = (int) v;
        y = (int) v1;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    private int y;
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public Vec2Int() {}
    public Vec2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public static Vec2Int readFrom(java.io.InputStream stream) throws java.io.IOException {
        Vec2Int result = new Vec2Int();
        result.x = StreamUtil.readInt(stream);
        result.y = StreamUtil.readInt(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeInt(stream, x);
        StreamUtil.writeInt(stream, y);
    }


    //0° is horizontal to the right. Counter-clockwise, radian!
    public static Vec2Int fromAngle(double alpha, double length) {
        return new Vec2Int(Math.cos(alpha) * length, Math.sin(alpha) * length);

    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public Vec2Int copy() {
        return new Vec2Int(x, y);
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

    public Vec2Int normalize() {
        double l = length();
        return scalar(1 / l);
    }

    public Vec2Int orthogonal() {
        return new Vec2Int(-y, x);
    }

    public Vec2Int add(Vec2Int v) {
        return new Vec2Int(x + v.x, y + v.y);
    }

    public Vec2Int subtract(Vec2Int v) {
        return new Vec2Int(x - v.x, y - v.y);
    }

    public Vec2Int scalar(double a) {
        return new Vec2Int(x * a, y * a);
    }

    public Vec2Int div(double con) {
        return new Vec2Int(this.x / con, this.y / con);
    }

    public double dotProduct(Vec2Int v) {
        return x * v.x + y * v.y;
    }

    public double angle() {
        double angle = (double) Math.atan2(y, x);
        // if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    public Vec2Int negate() {
        return new Vec2Int(-x, -y);
    }

    public boolean equals(Object o) {
        if (o instanceof Vec2Int) {
            Vec2Int v = (Vec2Int) o;
            return x == v.x && y == v.y;
        }
        return false;
    }

    public Vec2Int add(double a) {
        return new Vec2Int(x + a, y + a);
    }

    public Vec2Int add(int x1, int y1) {
        return new Vec2Int(x + x1, y + y1);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * @param d
     * @return returns a Vector with both components subtracted by d
     */
    public Vec2Int subtract(double d) {
        return new Vec2Int(x - d, y - d);
    }


    public Vec2Int addThis(Vec2Int speed) {
        this.x += speed.x;
        this.y += speed.y;
        return this;
    }

    public void setThisX(int x) {
        this.x = x;
    }

    public void setThisY(int y) {
        this.y = y;
    }

    public void addThisX(int x) {
        this.x += x;
    }

    public void addThisY(int y) {
        this.y += y;
    }

    public Vec2Float getVec2Float(){
        return new Vec2Float(x,y);
    }

}
