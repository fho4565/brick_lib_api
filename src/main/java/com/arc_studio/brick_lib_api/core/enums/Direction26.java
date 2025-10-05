package com.arc_studio.brick_lib_api.core.enums;

import net.minecraft.core.Vec3i;
/**
 * 一个方块周围26个方块的位置
 * */
public enum Direction26 {
    EAST(1, 0, 0),
    WEST(-1, 0, 0),
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    SOUTH(0, 0, 1),
    NORTH(0, 0, -1),

    EAST_UP(1, 1, 0),
    EAST_DOWN(1, -1, 0),
    WEST_UP(-1, 1, 0),
    WEST_DOWN(-1, -1, 0),
    EAST_SOUTH(1, 0, 1),
    EAST_NORTH(1, 0, -1),
    WEST_SOUTH(-1, 0, 1),
    WEST_NORTH(-1, 0, -1),
    UP_SOUTH(0, 1, 1),
    UP_NORTH(0, 1, -1),
    DOWN_SOUTH(0, -1, 1),
    DOWN_NORTH(0, -1, -1),

    EAST_UP_SOUTH(1, 1, 1),
    EAST_UP_NORTH(1, 1, -1),
    EAST_DOWN_SOUTH(1, -1, 1),
    EAST_DOWN_NORTH(1, -1, -1),
    WEST_UP_SOUTH(-1, 1, 1),
    WEST_UP_NORTH(-1, 1, -1),
    WEST_DOWN_SOUTH(-1, -1, 1),
    WEST_DOWN_NORTH(-1, -1, -1);

    private final int dx;
    private final int dy;
    private final int dz;

    Direction26(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
    public Vec3i offset(){
        return new Vec3i(dx, dy, dz);
    }
    public Direction26[] corners(){
        return new Direction26[]{
                EAST_UP_SOUTH,
                EAST_UP_NORTH,
                EAST_DOWN_SOUTH,
                EAST_DOWN_NORTH,
                WEST_UP_SOUTH,
                WEST_UP_NORTH,
                WEST_DOWN_SOUTH,
                WEST_DOWN_NORTH
        };
    }
}
