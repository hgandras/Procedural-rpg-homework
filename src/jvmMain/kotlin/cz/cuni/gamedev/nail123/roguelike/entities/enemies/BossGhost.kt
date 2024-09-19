package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.Vision
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.goSmartlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room
import kotlin.random.Random

//IDEA: This boss spawns other enemies, like rats and orcs. Until you kill all spawned enemies,
//it teleports around the room every few steps, but does not attack you. Otherwise, it just chases the player.
//
// The ghost is strong when it does not have any summons, and it is hard to damage it, however, you can
//chase it down while the summons are active, and deal big damage to it. Just be careful to not die to the summons
//The number of summons increases when the ghost has low hitpoints
class BossGhost(roomID : Int) : Boss(GameTiles.BOSS_GHOST, roomID),HasVision {
    override val blocksVision: Boolean = false
    override val visionRadius = 100
    override val maxHitpoints: Int = 25
    override var hitpoints: Int = 25
    override var attack: Int = 4
    override var defense: Int = 2
    override var statusEffect: Effect = NoEffect()
    override var weaponStatusEffect: Effect = NoEffect()

    var spawned_entities : MutableList<Enemy> = mutableListOf()
    var room = Room.rooms[roomID]

    enum class State{FLEEING,SPAWNS,COMBAT}
    var state : State = State.COMBAT
    val rnd = Random.Default

    //Parameters
    val STATE_CHANGE_CHANCE = 0.1
    val NUM_SPAWNS : Int = 4
    val TELEPORT_STEPS : Int = 5 //Adjust based on room size

    var steps_until_next_teleport = 0

    override fun update() {
        super.update()
        val playerPosition = area.player.position
        val inSameRoom = room.inRoom(playerPosition)
        //Heal if player exits the room
        if(!inSameRoom){
            hitpoints = maxHitpoints
            state = State.COMBAT
            return
        }

        if(state == State.COMBAT){
            val canSeePlayer = playerPosition in Vision.getVisiblePositionsFrom(area,position,visionRadius)
            if (canSeePlayer) {
                goSmartlyTowards(playerPosition)
            }
            if(rnd.nextFloat()<STATE_CHANGE_CHANCE)
                state = State.SPAWNS
        }
        else if(state == State.SPAWNS) {
            for(i in 1..NUM_SPAWNS) {
                val spawn = Rat(roomID)
                area.addEntity(spawn,room.area.randomPos())
                spawned_entities.addLast(spawn)
            }
            state = State.FLEEING
            return
        }
        else if(state == State.FLEEING) {
            steps_until_next_teleport++
            var deadEntities = 0
            if(steps_until_next_teleport == TELEPORT_STEPS) {
                moveTo(room.area.randomPos())
                steps_until_next_teleport = 0
            }
            //Check if spawns are alive
            for(spawn in spawned_entities)
            {
                if(spawn.hitpoints<=0)
                    deadEntities++
            }
            this.logMessage(deadEntities.toString())
            if(deadEntities == NUM_SPAWNS) {
                spawned_entities.clear()
                state = State.COMBAT
                steps_until_next_teleport = 0
            }
        }
    }
}