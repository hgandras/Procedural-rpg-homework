package cz.cuni.gamedev.nail123.roguelike.entities.objects

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.SortingLayer
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Interactable
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.WorldConfig

class Stairs(val leadDown: Boolean = true): GameEntity(
        if (leadDown) GameTiles.STAIRS_DOWN else GameTiles.STAIRS_UP,
        SortingLayer.STAIRS
    ), Interactable {

    override val blocksMovement = false
    override val blocksVision = false

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(InteractionType.STEPPED_ON) {
            if (leadDown && it.bossesKilled == WorldConfig.NUM_BOSSES) {
                area.world.moveDown()
            } else if(!leadDown) area.world.moveUp()
            else this.logMessage("Kill all bosses to move to the next level.")
        }
    }
}