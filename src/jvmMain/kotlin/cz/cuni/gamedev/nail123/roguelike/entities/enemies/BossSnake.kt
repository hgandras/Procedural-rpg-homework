package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.Vision
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Poison
import cz.cuni.gamedev.nail123.roguelike.mechanics.goSmartlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

//IDEA: The snake boss has a special healing ability, and is faster than you. The snake can also poison you
//with its attacks (reduces attack damage). Healing attacks start, when it loses about 30 percent of its hp
class BossSnake(roomID : Int) : Boss(GameTiles.BOSS_SNAKE,roomID),HasVision {
        override val blocksVision: Boolean = false
        override val visionRadius = 4
        override val maxHitpoints: Int = 30
        override var hitpoints: Int = 30
        override var attack: Int = 4
        override var defense: Int = 2
        override var statusEffect: Effect = NoEffect()
        override var weaponStatusEffect: Effect = NoEffect()

        enum class State{COMBAT,CHOOSE_POS,HEALING}

        var state :State = State.COMBAT
        var room = Room.rooms[roomID]
        var rnd = Random.Default
        var poison_step = 0
        var heal_percent_per_step = 0.0
        var heal_step = 0
        var lastHp = maxHitpoints

        val POISON_CHANCE = 0.1
        val POISON_COOLDOWN = 10
        val HEAL_DISTANCE_FROM_PLAYER = 6
        val HEAL_STEPS = 5
        val MAX_HEAL_PRECENT = 0.2
        val MIN_HEAL_PERCENT = 0.1
        val HEAL_MOVE_THRESHOLD = 0.3

        override fun update() {
                super.update()

                if(hitpoints<=0)
                        return

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

                        if(poison_step > 0) {
                                statusEffect = NoEffect()
                                poison_step++
                                if(poison_step == POISON_COOLDOWN)
                                        poison_step = 0
                        }

                        if(rnd.nextFloat() < POISON_CHANCE && poison_step == 0) {
                                weaponStatusEffect = Poison(1)
                                poison_step++
                        }

                        if(hitpoints/maxHitpoints<HEAL_MOVE_THRESHOLD && lastHp - hitpoints >= 15)
                                state = State.CHOOSE_POS
                }
                else if(state == State.CHOOSE_POS)
                {
                        var healPos = room.area.randomPos()
                        var diff = healPos - area.player.position
                        while(abs(diff.x) < HEAL_DISTANCE_FROM_PLAYER && abs(diff.y) < HEAL_DISTANCE_FROM_PLAYER )
                        {
                                healPos = room.area.randomPos()
                                diff = healPos - area.player.position
                        }
                        moveTo(healPos)
                        heal_percent_per_step = (MIN_HEAL_PERCENT + rnd.nextFloat() * (MAX_HEAL_PRECENT - MIN_HEAL_PERCENT)) * maxHitpoints / HEAL_STEPS
                        this.logMessage("Snake is healing")
                        state = State.HEALING
                }
                else if(state == State.HEALING)
                {
                        val healAmount = round(heal_percent_per_step).toInt()
                        hitpoints = min(maxHitpoints,hitpoints + healAmount)
                        heal_step++
                        this.logMessage("${"Snake healed for "}$healAmount")
                        if(heal_step == HEAL_STEPS || hitpoints == maxHitpoints)
                        {
                                heal_step = 0
                                lastHp = hitpoints
                                state = State.COMBAT
                        }
                }
        }
}