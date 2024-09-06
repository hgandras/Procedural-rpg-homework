package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats

class Poison(override val strength: Int, override val maxTime : Int = 1) : Effect {
    override val multiplier: Int = 1
    override var time: Int = 0
    override fun statusEffect(entity: HasCombatStats) {
        if(time == maxTime){
            time = 0
            entity.statusEffectApplied = false
            return
        }
        if(!entity.statusEffectApplied){
            entity.attack -= strength * multiplier
            entity.statusEffectApplied = true
        }
    }
}