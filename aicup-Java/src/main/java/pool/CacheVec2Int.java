package pool;

import model.Vec2Int;

public class CacheVec2Int {

        static Vec2Int[] mAvailable;
        static int size;
        static int offset;
        // ArrayList<Coord> mUse = new ArrayList<>();
        public CacheVec2Int(int size, int offset) {
            this.size = size;
            this.offset = offset;
            mAvailable = new Vec2Int[size*size];
            int i =0;
            for (int x=-offset; x<size-offset; x++)
            {
                for (int y=-offset; y<size-offset; y++)
                {
                    mAvailable[(x+offset)*size+(y+offset)] = new Vec2Int(x,y);
                }
            }
        }

        static public Vec2Int getVec2Int(int x, int y){

            if ((x+offset)*size+(y+offset)>size*size || (x+offset)*size+(y+offset)<0) {
              //  System.out.println("BAD x,y " + x + " " + y);
                return new Vec2Int(x,y);
            }
            return mAvailable[(x+offset)*size+(y+offset)];
        }
    }