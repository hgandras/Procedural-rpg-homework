package cz.cuni.gamedev.nail123.roguelike.world.worlds;


import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity;
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Enemy;
import org.hexworks.zircon.api.data.Position3D;

import java.util.ArrayList;

public class Room {

    public Box area;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    Room(Box box){
        area = box;
    }

    public int numEnemies()
    {
        return enemies.size();
    }

    public void addEnemy(Enemy enemy)
    {
        enemies.add(enemy);
    }

    public void removeEnemy(Enemy enemy){enemies.remove(enemy);}
    public boolean inRoom(Position3D position) { return position.getX() < area.maxX && position.getX() > area.minX && position.getY() > area.minY && position.getY() < area.maxY;}

    public static Room empty()
    {
        return new Room(new Box());
    }
    public static ArrayList<Room> rooms = new ArrayList<>();

}
