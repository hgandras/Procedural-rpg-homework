package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.mechanics.Vision
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.goSmartlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room


class Orc(roomID : Int = -1) : Enemy(GameTiles.ORC,roomID), HasVision{
    override val blocksVision = false
    override val visionRadius = 7

    override val maxHitpoints = 5
    override var hitpoints = 5
    override var attack = 1
    override var defense = 0
    override var statusEffect: Effect = NoEffect()
    override var weaponStatusEffect: Effect = NoEffect()
    var room = Room.rooms[roomID]
    var nextPos = room.area.randomPos()

    //If the orc can see the player it starts chasing it. Stays within the assigned room
    override fun update() {
        val prevPos = position

        val playerPosition = area.player.position

        val canSeePlayer = playerPosition in Vision.getVisiblePositionsFrom(area,position,visionRadius) && room.inRoom(playerPosition)
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