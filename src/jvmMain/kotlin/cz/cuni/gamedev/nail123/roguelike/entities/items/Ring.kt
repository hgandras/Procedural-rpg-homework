package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Interactable
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class Ring : GameEntity(GameTiles.RING), Interactable {
    override val blocksMovement: Boolean = false
    override val blocksVision: Boolean = false

    private fun pickUp(player: Player){
        player.bossesKilled+=1
        this.area.removeEntity(this)
    }

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type)  {
        withEntity<Player>(InteractionType.STEPPED_ON) { player: Player -> pickUp(player) }
    }
}