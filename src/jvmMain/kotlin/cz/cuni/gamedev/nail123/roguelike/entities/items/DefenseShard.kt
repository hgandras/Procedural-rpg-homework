package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Interactable
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class DefenseShard : GameEntity(GameTiles.DEFENSE_SHARD),Interactable {
    override val blocksMovement: Boolean = false
    override val blocksVision: Boolean = false

    val defenseUp : Int = 5

    private fun pickUp(player: Player){
        player.defense+= defenseUp
        this.area.removeEntity(this)
    }

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type)  {
        withEntity<Player>(InteractionType.STEPPED_ON) { player: Player -> pickUp(player) }
    }
}