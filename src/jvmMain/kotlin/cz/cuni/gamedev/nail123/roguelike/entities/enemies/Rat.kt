package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasSmell
import cz.cuni.gamedev.nail123.roguelike.entities.items.Sword
import cz.cuni.gamedev.nail123.roguelike.mechanics.Pathfinding
import cz.cuni.gamedev.nail123.roguelike.mechanics.goBlindlyTowards
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room

class Rat(room: Room): Enemy(GameTiles.RAT,room), HasSmell {
    override val blocksMovement = true
    override val blocksVision = false
    override val smellingRadius = 7

    override val maxHitpoints = 10
    override var hitpoints = 6
    override var attack = 3
    override var defense = 0
    override var statusEffectApplied: Boolean = false

    override fun update() {
        if (Pathfinding.chebyshev(position, area.player.position) <= smellingRadius) {
            goBlindlyTowards(area.player.position)
        }
    }
}