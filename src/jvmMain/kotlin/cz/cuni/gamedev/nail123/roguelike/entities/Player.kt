package cz.cuni.gamedev.nail123.roguelike.entities

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasVision
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory
import cz.cuni.gamedev.nail123.roguelike.events.GameOver
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.worlds.Room
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

class Player: MovingEntity(GameTiles.PLAYER), HasVision, HasCombatStats, HasInventory {
    override val visionRadius = 9
    override val blocksMovement = true
    override val blocksVision = false

    override var maxHitpoints = 25
    override var hitpoints = 25
    override var attack = 2
    override var defense = 1
    override var statusEffect: Effect = NoEffect()
    override var weaponStatusEffect : Effect = NoEffect()
    override val inventory = Inventory(this)

    var bossesKilled = 0
    var defenseShardPicked = false //Only one defense shard can be picked

    override fun die() {
        super.die()
        this.logMessage("You have died!")
        GameOver(this).emit()
    }
}