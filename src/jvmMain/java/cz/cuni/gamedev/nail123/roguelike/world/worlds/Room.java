package cz.cuni.gamedev.nail123.roguelike.world.worlds;


import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity;
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Enemy;

import java.util.ArrayList;

public class Room {
    public Box area;
    public int ID = -1;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    Room(Box box, int ID){
        area = box;
        this.ID = ID;
    }

    public int numEnemies()
    {
        return enemies.size();
    }

    public void addEnemy(Enemy enemy)
    {
        enemies.add(enemy);
    }

    public static Room empty()
    {
        return new Room(new Box(),-1);
    }

}
