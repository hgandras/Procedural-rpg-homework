package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.*
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import kotlin.math.min

class Potion : GameEntity(GameTiles.POTION), Interactable {
    override val blocksMovement: Boolean = false
    override val blocksVision: Boolean = false

    val healHP : Int = 5

    private fun pickUp(player:Player){
        player.hitpoints = min(player.hitpoints + healHP, player.maxHitpoints)
        this.area.removeEntity(this)
    }

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type)  {
        withEntity<Player>(InteractionType.STEPPED_ON) {player: Player -> pickUp(player) }
    }
}