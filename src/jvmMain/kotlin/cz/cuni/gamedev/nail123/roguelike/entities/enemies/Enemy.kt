package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.MovingEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.*
import cz.cuni.gamedev.nail123.roguelike.mechanics.Combat
import cz.cuni.gamedev.nail123.roguelike.mechanics.LootSystem
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room
import org.hexworks.zircon.api.data.Tile

abstract class Enemy(tile: Tile, val room : Room = Room.empty()): MovingEntity(tile), HasCombatStats, Interactable, Interacting {

    override val blocksMovement = true

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(InteractionType.BUMPED) { player -> Combat.attack(player, this@Enemy) }
    }

    override fun interactWith(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(InteractionType.BUMPED) { player -> Combat.attack(this@Enemy, player) }
    }

    override fun die() {
        super.die()
        LootSystem.onDeath(this)
    }

}