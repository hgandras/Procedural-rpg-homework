package cz.cuni.gamedev.nail123.roguelike.world.worlds;

public class WorldConfig {
    //If this is large, compared to the size the generated rooms the dungeon contains more of the maze.
    final static int X_ROOM_OFFSET_MAX = 10;
    final static int Y_ROOM_OFFSET_MAX = 3;

    //To control number of rooms
    final static int AREA_SPLITS = 4;

    //Room contents
    final static int MAX_ENEMIES = 2;
    final static double CHEST_CHANCE = 0.9;
    final public static int NUM_BOSSES = 3;
}
