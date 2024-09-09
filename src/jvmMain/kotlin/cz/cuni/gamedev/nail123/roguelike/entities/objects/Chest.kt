package cz.cuni.gamedev.nail123.roguelike.entities.objects

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.mechanics.Combat
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.*
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.LootSystem
import org.hexworks.cobalt.logging.api.Logger


class Chest : GameEntity(GameTiles.CHEST), Interactable {
    override val blocksMovement: Boolean
        get() = true
    override val blocksVision: Boolean
        get() = false

    var opened = false

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(InteractionType.BUMPED) {
            if(!opened) {
                this.logMessage("opened")
                opened = true
                LootSystem.onInteract(this@Chest)
                this@Chest.area.removeEntity(this@Chest)
            }
        }
    }
}