package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import kotlin.math.max

class FireEffect(override val strength: Int, override val maxTime: Int) : Effect {
    override val multiplier = 1
    override var time = 0
    override fun statusEffect(entity: HasCombatStats){
        if(time == maxTime){
            time = 0
            entity.statusEffectApplied = false
            return
        }
        entity.hitpoints-=multiplier * strength
        entity.statusEffectApplied = true
    }
}