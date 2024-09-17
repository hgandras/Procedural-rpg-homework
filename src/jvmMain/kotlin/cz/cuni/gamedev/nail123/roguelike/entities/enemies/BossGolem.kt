package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.Vision
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.mechanics.goSmartlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room
import kotlin.math.abs
import kotlin.random.Random

//IDEA:The golem is going for you with large AOE attacks that deal damage in a square around it,
// but there is a log warning before it does something, so these can be dodged,
//and they have a cast time. The golem can not attack(its attack damage becomes 0) after
//a big ability, because it needs to rest. That is the time of window to kill it. Otherwise, it does basic combat,
//and has huge defense. It only does the charge attack if the player is close.
class BossGolem(roomID:Int) : Boss(GameTiles.BOSS_GOLEM,roomID),HasVision {
    override val blocksVision: Boolean = false
    override val visionRadius = 5
    override val maxHitpoints: Int = 25

    val ORIGINAL_DEFENSE = 3
    val ORIGINAL_ATTACK = 3

    override var hitpoints: Int = 25
    override var attack: Int = ORIGINAL_ATTACK
    override var defense: Int = ORIGINAL_DEFENSE
    override var statusEffect: Effect = NoEffect()
    override var weaponStatusEffect: Effect = NoEffect()


    enum class State{COMBAT,RESTING,CHARGE_ATTACK}

    var state :State = State.COMBAT
    var room = Room.rooms[roomID]
    var lastHp = maxHitpoints
    var charge_step = 0
    var rest_step = 0

    //Constants
    val CHARGE_TIME = 5 //Set these two to make sense
    val EMPOWERED_ATTACK_RADIUS = 4
    val EMPOWERED_ATTACK_DAMAGE = 20
    val REST_TIME = 7 //More than the radius, so you can do some damage
    val WEAKENED_ATTACK = 0
    val WEAKENED_DEFENSE = 0

    override fun update() {
        super.update()

        val playerPosition = area.player.position
        val inSameRoom = room.inRoom(playerPosition)
        //Heal if player exits the room
        if(!inSameRoom){
            hitpoints = maxHitpoints
            defense = ORIGINAL_DEFENSE
            attack = ORIGINAL_ATTACK
            state = State.COMBAT
            return
        }

        if(state == State.COMBAT){
            val canSeePlayer = playerPosition in Vision.getVisiblePositionsFrom(area,position,visionRadius)
            val diff = area.player.position-position
            if( abs(diff.x) < EMPOWERED_ATTACK_RADIUS && abs(diff.y) < EMPOWERED_ATTACK_RADIUS  && lastHp - hitpoints >= 5) {
                this.logMessage("AAAUUAAAARRGGGGHHH!!!!!") //Message to not make it too obvious that you have to go away
                state = State.CHARGE_ATTACK
                defense = 10000
                attack = 0
            }

            if (canSeePlayer) {
                goSmartlyTowards(playerPosition)
            }
        }

        else if(state == State.CHARGE_ATTACK) {
            charge_step++
            if (charge_step == CHARGE_TIME)
            {
                //maybe come up with different AOE attacks
                val diff = area.player.position-position
                if(abs(diff.x) < EMPOWERED_ATTACK_RADIUS && abs(diff.y) < EMPOWERED_ATTACK_RADIUS)
                {
                    area.player.takeDamage(EMPOWERED_ATTACK_DAMAGE)
                }
                charge_step = 0
                this.logMessage("The Golem seems tired")
                state = State.RESTING
                defense = WEAKENED_DEFENSE
                attack = WEAKENED_ATTACK
            }
        }
        else if(state == State.RESTING)
        {
            rest_step++
            if(rest_step == REST_TIME)
            {
                rest_step = 0
                defense = ORIGINAL_DEFENSE
                attack = ORIGINAL_ATTACK
                lastHp = hitpoints
                state = State.COMBAT
            }
        }
    }
}

