package cz.cuni.gamedev.nail123.roguelike.world.worlds;

public class WorldConfig {
    //If this is large, compared to the size the generated rooms the dungeon contains more of the maze.
    final static int X_ROOM_OFFSET_MAX = 10;
    final static int Y_ROOM_OFFSET_MAX = 3;

    //To control number of rooms
    final static int AREA_SPLITS = 5;

    //Room contents
    final static int MAX_ENEMIES = 3;
    final static int MAX_CHESTS = 1;
    final static int MAX_MOBS_PER_ROOM = 2;
}
