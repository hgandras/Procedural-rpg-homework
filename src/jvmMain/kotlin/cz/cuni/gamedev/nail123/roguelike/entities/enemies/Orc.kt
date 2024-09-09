package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.mechanics.Vision
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.goSmartlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room


class Orc(room : Room = Room.empty()) : Enemy(GameTiles.ORC,room), HasVision{
    override val blocksVision = false
    override val visionRadius = 7

    override val maxHitpoints = 5
    override var hitpoints = 5
    override var attack = 1
    override var defense = 0
    override var statusEffect: Effect = NoEffect()

    var nextPos = room.area.randomPos()

    //If the orc can see the player it starts chasing it. Stays within the assigned room
    override fun update() {
        val prevPos = position

        val playerPosition = area.player.position
        val playerInSameRoom = playerPosition.x < room.area.maxX && playerPosition.x> room.area.minX && playerPosition.y < room.area.maxY && playerPosition.y>room.area.minY

        val canSeePlayer = playerPosition in Vision.getVisiblePositionsFrom(area,position,visionRadius) && playerInSameRoom
        if (canSeePlayer) {
            goSmartlyTowards(playerPosition)
        }
        else
        {
            val arrived = position.compareTo(nextPos)
            if(arrived == 0 || prevPos == position){
                nextPos = room.area.randomPos()
            }
            goSmartlyTowards(nextPos)
            //If stuck
            if(position.compareTo(prevPos) == 0)
                nextPos = room.area.randomPos()
        }
    }
}