package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.Effect
import cz.cuni.gamedev.nail123.roguelike.mechanics.effects.NoEffect
import org.hexworks.zircon.api.data.Tile

abstract class Weapon(tile: Tile, val effect : Effect = NoEffect(), name : String = ""): Item(tile, name) {
    override fun isEquipable(character: HasInventory): Inventory.EquipResult {
        return if (character.inventory.equipped.filterIsInstance<Weapon>().isNotEmpty()) {
            Inventory.EquipResult(false, "Cannot equip two weapons")
        } else Inventory.EquipResult.Success
    }
}