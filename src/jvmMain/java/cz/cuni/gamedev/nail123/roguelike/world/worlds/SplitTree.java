package cz.cuni.gamedev.nail123.roguelike.world.worlds;

import java.util.ArrayList;
import kotlin.Pair;

public class SplitTree {
    ArrayList<Node> nodes = new ArrayList<Node>();
    Node root;
    int depth;

    SplitTree(Box baseArea,int depth)
    {
        this.root = new Node(-1,baseArea);
        nodes.add(root);
        this.depth = depth;
        build(0,0);
    }

    private void build(int parentID, int currentDepth)
    {
        if(currentDepth == depth)
            return;
        currentDepth++;
        Node parent = nodes.get(parentID);
        String axis = "x";
        if(currentDepth % 2 == 1)
            axis = "y";
        Pair<Box,Box> newAreas = parent.box.Split(axis,"");

        Node child1 = new Node(parentID,newAreas.component1());
        nodes.add(child1);
        build(nodes.size()-1,currentDepth);

        Node child2 = new Node(parentID,newAreas.component2());
        nodes.add(child2);
        build(nodes.size()-1,currentDepth);

        parent.children = new Pair<Node,Node>(child1,child2);
    }

    class Node{
        int parent;
        Box box;
       Pair<Node,Node> children;

       Node(int parent, Box box)
       {
           this.parent = parent;
           this.box = box;
       }
    }

}
