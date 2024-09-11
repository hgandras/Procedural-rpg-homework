package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import cz.cuni.gamedev.nail123.roguelike.events.logMessage

class FireEffect(override val strength: Int) : Effect {
    override val maxTime = 5
    override val multiplier = 1
    override var time = 0
    override fun tick(entity: HasCombatStats){
        if(time == maxTime){
            time = 0
            entity.statusEffect = NoEffect()
            return
        }
        this.logMessage("burnt for 1 hp")
        time++
        entity.hitpoints-=multiplier * strength
    }
}