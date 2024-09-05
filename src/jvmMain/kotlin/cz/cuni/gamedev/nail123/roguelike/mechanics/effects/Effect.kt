package cz.cuni.gamedev.nail123.roguelike.mechanics.effects

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats

interface Effect {
    val strength : Int
    val multiplier : Int
    val maxTime: Int

    var time: Int

     fun statusEffect(entity : HasCombatStats)
}