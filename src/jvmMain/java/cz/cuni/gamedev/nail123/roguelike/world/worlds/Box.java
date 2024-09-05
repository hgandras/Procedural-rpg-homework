package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import kotlin.Pair;
import org.hexworks.zircon.api.data.Position3D;

import java.util.Random;

public class Box {
    public float minX,minY,maxX,maxY;

    Box()
    {
        minX = 0;
        minY = 0;
        maxX = 1;
        maxY = 1;
    }

    Box(float minX, float minY, float maxX, float maxY)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    Pair<Box,Box> Split(String axis,String method)
    {

        float splitPoint = 0.5f;
        if(method.equals("random"))
        {
            Random rnd = new Random();
            splitPoint = rnd.nextFloat();
        }
        Box box1 = new Box(),box2 = new Box();
        if(axis.equals("x")) {
            float diff = maxX-minX;
            float splitX = minX + diff * splitPoint;
            box1 = new Box(minX,minY,splitX,maxY);
            box2 = new Box(splitX,minY,maxX,maxY);
        }
        else if(axis.equals("y")) {
            float diff = maxY - minY;
            float splitY = minY + diff * splitPoint;
            box1 = new Box(minX, splitY, maxX, maxY);
            box2 = new Box(minX, minY, maxX, splitY);
        }
        return new Pair<Box,Box>(box1,box2);
    }

    public Position3D randomPos(){
        Random rnd = new Random();
        return Position3D.create(rnd.nextInt((int)minX + 1,(int)maxX),rnd.nextInt((int)minY + 1, (int)maxY),0);
    }
}
