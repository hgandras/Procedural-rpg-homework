package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import java.util.*;

public class Maze {

    enum Directions{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    class Field{
        int x,y;
        boolean[] connections = new boolean[]{false,false,false,false};;
        Field(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    ArrayList<Field> fields = new ArrayList<Field>();
    int height, width;
    Random rnd = new Random();

    Maze(int height, int width){
        this.height = height;
        this.width = width;
    }

    private boolean isOutside(int X, int Y)
    {
        return X < 0 || X > width - 1 || Y < 0 || Y > height - 1;
    }

    //Generates a maze by backtracking
    public void Generate()
    {
        Stack<Field> field_stack = new Stack<Field>();
        boolean[] visited = new boolean[height * width];
        Arrays.fill(visited,false);
        Random rnd = new Random();

        Field currentField = new Field(rnd.nextInt(width),rnd.nextInt(height));
        visited[currentField.y * width + currentField.x] = true;
        field_stack.push(currentField);
        Field nextField = new Field(-1,-1);
        ArrayList<Directions> possibleDirs = new ArrayList<Directions>();

        while(!field_stack.isEmpty())
        {
            //1. Get possible directions
            possibleDirs.clear();
            if (!isOutside(currentField.x - 1, currentField.y) && !visited[currentField.y * width + currentField.x - 1]) possibleDirs.add(Directions.UP);
            if (!isOutside(currentField.x + 1, currentField.y) && !visited[currentField.y * width + currentField.x + 1]) possibleDirs.add(Directions.DOWN);
            if (!isOutside(currentField.x, currentField.y - 1 ) && !visited[(currentField.y - 1) * width + currentField.x]) possibleDirs.add(Directions.LEFT);
            if (!isOutside(currentField.x, currentField.y + 1 ) && !visited[(currentField.y + 1) * width + currentField.x]) possibleDirs.add(Directions.RIGHT);

            //2. If not 0, pick one, else backtrack until there is one with a possible dir.
            if(possibleDirs.isEmpty())
            {
                currentField = field_stack.pop();
                fields.add(currentField);
                continue;
            }

            int dirID = rnd.nextInt(possibleDirs.size());
            Directions dir = possibleDirs.get(dirID);

            //3.Create new field, and update previous's connection
            switch (dir)
            {
                case Directions.UP:
                    nextField = new Field(currentField.x - 1 , currentField.y);
                    visited[currentField.y * width + currentField.x - 1] = true;
                    currentField.connections[Directions.UP.ordinal()] = true;
                    nextField.connections[Directions.DOWN.ordinal()] = true;
                    break;

                case Directions.DOWN:
                    nextField = new Field(currentField.x + 1, currentField.y);
                    visited[currentField.y * width + currentField.x + 1] = true;
                    currentField.connections[Directions.DOWN.ordinal()] = true;
                    nextField.connections[Directions.UP.ordinal()] = true;
                    break;

                case Directions.LEFT:
                    nextField = new Field(currentField.x, currentField.y - 1);
                    visited[(currentField.y - 1) * width + currentField.x ] = true;
                    currentField.connections[Directions.LEFT.ordinal()] = true;
                    nextField.connections[Directions.RIGHT.ordinal()] = true;
                    break;

                case Directions.RIGHT:
                    nextField = new Field(currentField.x, currentField.y + 1);
                    visited[(currentField.y + 1) * width + currentField.x ] = true;
                    currentField.connections[Directions.RIGHT.ordinal()] = true;
                    nextField.connections[Directions.LEFT.ordinal()] = true;
                    break;
            }

            field_stack.push(nextField);
            currentField = nextField;
        }
    }







}
