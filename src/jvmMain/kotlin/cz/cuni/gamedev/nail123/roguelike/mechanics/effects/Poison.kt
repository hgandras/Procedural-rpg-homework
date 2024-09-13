package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import cz.cuni.gamedev.nail123.roguelike.events.logMessage

class Poison(override val strength: Int) : Effect {
    override val multiplier: Int = 1
    override val maxTime : Int = 5
    override var time: Int = 0
    var appliedOnce :Boolean = false

    override fun tick(entity: HasCombatStats) {
        if(time == maxTime){
            entity.attack += strength * multiplier
            time = 0
            appliedOnce = false
            entity.statusEffect = NoEffect()
            return
        }
        if(!appliedOnce){
            this.logMessage("$entity${" poisoned"}")
            entity.attack -= strength * multiplier
            appliedOnce = true
        }
        time++
    }
}