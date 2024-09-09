package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats

class NoEffect : Effect {
    override val strength = 0
    override val multiplier = 0
    override val maxTime = 0
    override var time = 0

    override fun tick(entity: HasCombatStats) {
        return
    }
}