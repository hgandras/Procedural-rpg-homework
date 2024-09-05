package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import cz.cuni.gamedev.nail123.roguelike.GameConfig;
import cz.cuni.gamedev.nail123.roguelike.blocks.Floor;
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock;
import cz.cuni.gamedev.nail123.roguelike.blocks.Wall;
import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity;
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Enemy;
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Orc;
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Rat;
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Chest;
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Stairs;
import cz.cuni.gamedev.nail123.roguelike.events.LoggedEvent;
import cz.cuni.gamedev.nail123.roguelike.mechanics.Pathfinding;
import cz.cuni.gamedev.nail123.roguelike.world.Area;
import cz.cuni.gamedev.nail123.roguelike.world.World;
import cz.cuni.gamedev.nail123.roguelike.world.builders.AreaBuilder;
import cz.cuni.gamedev.nail123.roguelike.world.builders.EmptyAreaBuilder;
import cz.cuni.gamedev.nail123.utils.collections.ObservableMap;
import kotlin.Pair;
import org.hexworks.zircon.api.data.Position3D;
import org.hexworks.zircon.api.data.Size3D;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SampleJavaWorld extends World {
    int currentLevel = 0;

    public SampleJavaWorld() {
    }

    @NotNull
    @Override
    public Area buildStartingArea() {
        return buildLevel();
    }

    Area buildLevel() {
        // Start with an empty area
        AreaBuilder areaBuilder = (new EmptyAreaBuilder()).create();

        Box baseArea = new Box(0,0,areaBuilder.getWidth()-2,areaBuilder.getHeight()-2);
        SplitTree wallsTree = new SplitTree(baseArea,GameConfig.AREA_SPLITS);

        ObservableMap<Position3D, GameBlock> blocksMap;
        GameBlock[] blocks = new GameBlock[areaBuilder.getWidth() * areaBuilder.getHeight()];
        ArrayList<Room> rooms = new ArrayList<>();

        //PLACING MAZE
        Maze maze = new Maze((areaBuilder.getHeight() - 1) / 2,   (areaBuilder.getWidth() -1)/ 2);
        maze.Generate();

        //1.Build wall around it
        for(int y = 0; y <= maze.height * 2 ; y++)
            for (int x = 0; x <= maze.width * 2; x++) {
                if (y == 0 || y == maze.height * 2 + 1 || x == 0 || x == maze.width * 2 + 1)
                    blocks[ x + y * areaBuilder.getWidth() ] = new Wall();
                else if (x % 2 == 0 || y % 2 == 0)
                    blocks[ x + y * areaBuilder.getWidth() ] = new Wall();
                else
                    blocks[ x + y * areaBuilder.getWidth() ] = new Floor();
            }

        for(Maze.Field field: maze.fields)
        {
            int counter = 0;
            for(boolean connected : field.connections)
            {
                if(connected)
                {
                    int x = field.x * 2 + 1;
                    int y = field.y * 2 + 1;
                    Maze.Directions dir = Maze.Directions.values()[counter];
                    switch(dir)
                    {
                        case Maze.Directions.UP:
                            blocks[ x - 1 + y * areaBuilder.getWidth() ] = new Floor();
                            break;

                        case Maze.Directions.DOWN:
                            blocks[ x + 1 + y * areaBuilder.getWidth() ] = new Floor();
                            break;

                        case Maze.Directions.LEFT:
                            blocks[ x + (y - 1) * areaBuilder.getWidth() ] = new Floor();
                            break;

                        case Maze.Directions.RIGHT:
                            blocks[ x + (y + 1) * areaBuilder.getWidth() ] = new Floor();
                            break;
                    }
                }
                counter++;
            }
        }

        //PLACING ROOMS
        Random rnd = new Random();
        int roomID = 0;
        for(SplitTree.Node node: wallsTree.nodes)
        {
            //If it is a leaf node, create random offsets for the room sizes
            if(node.children == null)
            {
                int offsetXMin = rnd.nextInt(GameConfig.X_ROOM_OFFSET_MAX), offsetXMax = rnd.nextInt(GameConfig.X_ROOM_OFFSET_MAX), offsetYMin = rnd.nextInt(GameConfig.Y_ROOM_OFFSET_MAX), offsetYMax = rnd.nextInt(GameConfig.Y_ROOM_OFFSET_MAX);
                int roomMinX = (int)Math.floor(node.box.minX) + offsetXMin;
                int roomMaxX = (int)Math.floor(node.box.maxX) - offsetXMax;
                int roomMinY = (int)Math.floor(node.box.minY) + offsetYMin;
                int roomMaxY = (int)Math.floor(node.box.maxY) - offsetYMax;

                //Save the room
                rooms.add(new Room(new Box(roomMinX,roomMinY,roomMaxX,roomMaxY),roomID));

                for(int x = roomMinX + 1; x < roomMaxX ;x++)
                {
                    for(int y = roomMinY + 1;y < roomMaxY;y++)
                    {
                        blocks[ x + y * areaBuilder.getWidth() ] = new Floor();
                    }
                }
                roomID++;
            }
        }

        //Add start and end positions
        Box start_room = rooms.getFirst().area;
        Box goal_room = rooms.getLast().area;

        Position3D goal = Position3D.create((int)goal_room.minX + 1, (int)goal_room.minY + 1, 0);
        System.out.println(goal.getX() + " " + goal.getY());
        System.out.println(blocks[goal.getY() * areaBuilder.getWidth() + goal.getX()]);
        //GENERATE MAP
        blocksMap = generateMap(areaBuilder.getWidth(), areaBuilder.getHeight(), blocks);

        areaBuilder.setBlocks(blocksMap);

        //ADDING ROOM WALLS
        System.out.println(rooms.size());
        //Shuffling the rooms, and the wall coordinates later is important to create more diverse room patterns, because if they are not shuffled,
        //the checking by the floodfill will always generate a similar pattern with the room exits.
        Collections.shuffle(rooms);
        for(Room room: rooms)
        {
            ArrayList<Pair<Integer,Integer>> wallCoords = getWallCoords(room.area);
            Collections.shuffle(wallCoords);
            for(Pair<Integer,Integer> xy : wallCoords)
            {
                int x = xy.getFirst();
                int y = xy.getSecond();
                //If the maze has a gap at the wall of a room, check if the maze is still possible
                //to complete by closing that gap.
                if(blocks[y * areaBuilder.getWidth() + x] instanceof Floor)
                {
                    Position3D currentRoom = Position3D.create((int)room.area.minX + 1, (int)room.area.minY + 1,0);

                    blocks[y*areaBuilder.getWidth() + x] = new Wall();
                    blocksMap = generateMap(areaBuilder.getWidth(), areaBuilder.getHeight(), blocks);
                    areaBuilder.setBlocks(blocksMap);
                    Map<Position3D,Integer> reached_positions = Pathfinding.INSTANCE.floodFill(currentRoom,areaBuilder,Pathfinding.INSTANCE.getFourDirectional(),Pathfinding.INSTANCE.getDoorOpening());

                    int reachedPoss = getReachedPoss(reached_positions, goal, rooms);

                    if(reachedPoss<rooms.size() + 1)
                    {
                        blocks[y*areaBuilder.getWidth() + x] = new Floor();
                        blocksMap = generateMap(areaBuilder.getWidth(), areaBuilder.getHeight(), blocks);
                        areaBuilder.setBlocks(blocksMap);
                    }
                }
            }
        }

        placeInRoom(areaBuilder, start_room, areaBuilder.getPlayer());

        // Place the stairs at an empty location in the top-right quarter
        placeInRoom(areaBuilder, goal_room, new Stairs());

        //Add entities to rooms
        int roomNum = 0;
        for(Room room : rooms)
        {
            placeEnemies(areaBuilder,room);
            placeChests(areaBuilder,room);
            roomNum++;
        }
        return areaBuilder.build();
    }

    private static int getReachedPoss(Map<Position3D, Integer> reached_positions, Position3D goal, ArrayList<Room> rooms) {
        int reachedPoss = 0;
        for(Position3D pos : reached_positions.keySet())
        {
            //Check if goal can be reached
            if(pos.getX() == goal.getX() && pos.getY() == goal.getY())
            {
                reachedPoss++;
            }

            //Check if all other rooms can be reached
            for(Room roomPos : rooms)
            {
                if(roomPos.area.minX + 1 == pos.getX() && roomPos.area.minY + 1 == pos.getY())
                {
                    reachedPoss++;
                }
            }

        }
        return reachedPoss;
    }

    @NotNull
    private ObservableMap<Position3D,GameBlock> generateMap(int width, int height, GameBlock[] blocks){
        ObservableMap<Position3D, GameBlock> blocksMap = new ObservableMap<>();
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
            {
                int index = y * width + x;
                if(blocks[index] != null)
                    blocksMap.put(Position3D.create(x,y,0),blocks[index]);
            }
        return blocksMap;
    }

    private ArrayList<Pair<Integer,Integer>> getWallCoords(Box room)
    {
        ArrayList<Pair<Integer,Integer>> wallCoords = new ArrayList<>();
        for(int x = (int)room.minX; x<=room.maxX;x++) {
            for (int y = (int) room.minY; y <= room.maxY; y++) {
                //If the maze has a gap at the wall of a room, check if the maze is still possible
                //to complete by closing that gap.
                if ((x == room.minX || y == room.minY || x == room.maxX || y == room.maxY)) {
                    wallCoords.add(new Pair<>(x, y));
                }
            }
        }
        return wallCoords;
    }

    private void placeInRoom(AreaBuilder area, Box room, GameEntity entity)
    {
        area.addAtEmptyPosition(
                entity,
                Position3D.create((int)room.minX, (int)room.minY, 0),
                Size3D.create((int)room.maxX-(int)room.minX, (int)room.maxY - (int)room.minY, 1)
        );
    }

    private void placeEnemies(AreaBuilder area, Room room)
    {
        Random rnd = new Random();
        int numEnemies = rnd.nextInt(1,GameConfig.MAX_ENEMIES);
        for(int i = 0; i<numEnemies; i++)
        {
            Enemies enemyType = Enemies.values()[rnd.nextInt(Enemies.values().length)];
            Enemy enemy = new Rat(room);
            switch (enemyType)
            {
                case Enemies.RAT:
                    enemy = new Rat(room);
                    break;
                case Enemies.ORC:
                    enemy = new Orc(room);
                    room.addEnemy(new Orc(room));
                    break;
            }
            room.addEnemy(enemy);
            placeInRoom(area,room.area,enemy);
        }
    }

    private void placeChests(AreaBuilder area, Room room)
    {
        Random rnd = new Random();
        int numChests = rnd.nextInt(GameConfig.MAX_CHESTS+1);
        for(int i = 0; i<numChests; i++)
        {
            placeInRoom(area,room.area,new Chest());
        }
    }

    /**
     * Moving down - goes to a brand new level.
     */
    @Override
    public void moveDown() {
        ++currentLevel;
        (new LoggedEvent(this, "Descended to level " + (currentLevel + 1))).emit();
        if (currentLevel >= getAreas().getSize()) getAreas().add(buildLevel());
        goToArea(getAreas().get(currentLevel));
    }

    /**
     * Moving up would be for revisiting past levels, we do not need that. Check [DungeonWorld] for an implementation.
     */
    @Override
    public void moveUp() {
        // Not implemented
    }
}
