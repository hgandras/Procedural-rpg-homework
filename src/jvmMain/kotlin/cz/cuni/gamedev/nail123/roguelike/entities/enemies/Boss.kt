package cz.cuni.gamedev.nail123.roguelike.entities.enemies

import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import org.hexworks.zircon.api.data.Tile

abstract class Boss(tile: Tile, roomID : Int) : Enemy(tile,roomID) {
    override fun die() {
        super.die()
        area.player.bossesKilled++
        area.player.hitpoints = area.player.maxHitpoints
        this.logMessage("Boss killed! Health restored!")
    }
}