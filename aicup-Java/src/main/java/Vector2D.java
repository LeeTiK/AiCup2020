import model.Vec2Int;

public class Vector2D extends Vec2Int {
        public double x, y;

        public Vector2D(double x, double y) {
            this.x = (double) x;
            this.y = (double) y;
        }

        public Vector2D(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vector2D(Vec2Int position) {
            this.x = position.getX();
            this.y = position.getY();
        }

        public Vector2D(Vector2D startPosition) {
            this.x = startPosition.getX();
            this.y = startPosition.getY();
        }


        //0° is horizontal to the right. Counter-clockwise, radian!
        public static Vector2D fromAngle(double alpha, double length) {
             return new Vector2D(Math.cos(alpha) * length, Math.sin(alpha) * length);

        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }

        public Vector2D copy() {
            return new Vector2D(x, y);
        }

        public double distance(Vector2D v) {
            return subtract(v).length();
        }

        public double length() {
            return (double) Math.sqrt(x * x + y * y);
        }

        public double squaredLength() {
            return dotProduct(this);
        }

        public Vector2D normalize() {
            double l = length();
            return scalar(1 / l);
        }

        public Vector2D orthogonal() {
            return new Vector2D(-y, x);
        }

        public Vector2D add(Vector2D v) {
            return new Vector2D(x + v.x, y + v.y);
        }

        public Vector2D subtract(Vector2D v) {
            return new Vector2D(x - v.x, y - v.y);
        }

        public Vector2D scalar(double a) {
            return new Vector2D(x * a, y * a);
        }

        public Vector2D div(double con) {
            return new Vector2D(this.x / con, this.y / con);
        }

        public double dotProduct(Vector2D v) {
            return x * v.x + y * v.y;
        }

        public double angle() {
            double angle = (double) Math.atan2(y, x);
            // if (angle < 0) angle += 2 * Math.PI;
            return angle;
        }

        public Vector2D negate() {
            return new Vector2D(-x, -y);
        }

        public boolean equals(Object o) {
            if (o instanceof Vector2D) {
                Vector2D v = (Vector2D) o;
                return x == v.x && y == v.y;
            }
            return false;
        }

        public Vector2D add(double a) {
            return new Vector2D(x + a, y + a);
        }

        public String toString() {
            return "(" + x + "," + y + ")";
        }

        /**
         * @param d
         * @return returns a Vector with both components subtracted by d
         */
        public Vector2D subtract(double d) {
            return new Vector2D(x - d, y - d);
        }


        public Vector2D addThis(Vector2D speed) {
            this.x += speed.x;
            this.y += speed.y;
            return this;
        }

        public void setThisX(double x) {
            this.x = x;
        }

        public void setThisY(double y) {
            this.y = y;
        }

        public void addThisX(double x) {
            this.x += x;
        }

        public void addThisY(double y) {
            this.y += y;
        }

    }
