package model;

import util.StreamUtil;

public enum EntityType {
    WALL(0),
    HOUSE(1),
    BUILDER_BASE(2),
    BUILDER_UNIT(3),
    MELEE_BASE(4),
    MELEE_UNIT(5),
    RANGED_BASE(6),
    RANGED_UNIT(7),
    RESOURCE(8),
    TURRET(9),
    NO_ATTACK_ENTITY(0xFE),
    ATTACK_ENTITY(0xFD),
    ALL(0xFE),
    Empty(0xFF);
    public int tag;
    EntityType(int tag) {
      this.tag = tag;
    }
}
