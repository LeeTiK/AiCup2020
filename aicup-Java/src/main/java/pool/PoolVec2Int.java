package pool;

import model.Vec2Int;

public class PoolVec2Int {
        Vec2Int[] mAvailable;
        // ArrayList<Coord> mUse = new ArrayList<>();

        int number;

        int mStartSize;

        public PoolVec2Int(int startSize) {
            mAvailable = new Vec2Int[startSize];

            mStartSize = startSize;

            for (int i = 0; i < mStartSize; i++) mAvailable[i] = new Vec2Int(0,0);
        }

       /* void init() {
            for (int i = 0; i < mStartSize; i++) mAvailable.add(new Coord(0,0));
        }*/

        public Vec2Int getObject(int x, int y) {

            return getObjectV(x,y);
          /*  if (mAvailable.size() > 0) {
                Coord coord = mAvailable.get(mAvailable.size() - 1);
                coord.x = x;
                coord.y = y;
               // mUse.add(coord);
                mAvailable.remove(mAvailable.size() - 1);
                return coord;
            } else {
                Coord object = new Coord(x,y);
                mUse.add(object);
                return object;
            }*/
        }

        public Vec2Int getObjectV(int x, int y){
            //return new Vector2D(x,y);

            if (number<mAvailable.length) {
                Vec2Int vector2D = mAvailable[number];
                vector2D.setX(x);
                vector2D.setY(y);
                number++;
                return vector2D;
            }
            else {
                number++;
                return new Vec2Int(x,y);
            }
        }

        public void ReleaseObject(Vec2Int coord) {

            //   mUse.remove(coord);
            //  mAvailable.add(coord);
        }

        public void clearAll(){
            number = 0 ;
        }
    }